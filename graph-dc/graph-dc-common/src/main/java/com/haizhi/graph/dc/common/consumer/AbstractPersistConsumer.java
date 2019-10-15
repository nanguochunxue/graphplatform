package com.haizhi.graph.dc.common.consumer;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.dc.core.constant.DcConstants;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.common.model.DcInboundErrorInfo;
import com.haizhi.graph.dc.common.model.DcInboundResult;
import com.haizhi.graph.dc.common.monitor.MonitorService;
import com.haizhi.graph.dc.common.service.TaskRedisService;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.bean.Schema;
import com.haizhi.graph.dc.core.service.DcMetadataCache;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Created by chengmo on 2018/10/29.
 */
@Component
public abstract class AbstractPersistConsumer {
    private static final GLog LOG = LogFactory.getLogger(AbstractPersistConsumer.class);
    private static final int MAX_RETRIES = 3;

    protected StoreType storeType;

    @Autowired
    private DcMetadataCache dcMetadataCache;

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private TaskRedisService taskRedisService;

    protected void processMessages(List<ConsumerRecord<String, String>> records, Acknowledgment ack, StoreType
            storeType) {
        ack.acknowledge();
        for (ConsumerRecord<String, String> record : records) {
            DcInboundDataSuo suo = null;
            try {
                suo = JSON.parseObject(record.value(), DcInboundDataSuo.class);
                String graph = suo.getGraph();
                String schema = suo.getSchema();
                LOG.audit("receive data from kafka; graph={0}, schema={1}, rowsSize={2}",
                        graph, schema, suo.getRowsSize());

                Schema sch = getSchema(suo);
                if (Objects.isNull(sch)) {
                    LOG.error("schema not found, graph={0}, schema={1}", graph, schema);
                    return;
                }
                if (!needsPersist(sch, storeType)) {
                    return;
                }
                if (taskRedisService.overErrorMode(suo)) {
                    LOG.audit("taskInstanceId={0} over set error mode, the task not continue execution",
                            suo.getHeaderOptions().getString(DcConstants.KEY_TASK_INSTANCE_ID));
                    return;
                }
                DcInboundResult result = tryProcessMessages(suo);
                this.onInboundFinished(result, suo);
            } catch (Exception ex) {
                LOG.error("", ex);
                if (suo != null) {
                    LOG.error("process data got exception; graph={0}, schema={1}, rowsSize={2}",
                            suo.getGraph(), suo.getSchema(), suo.getRowsSize());
                }
            }
        }
    }

    protected void onInboundFinished(DcInboundResult result, DcInboundDataSuo cuo) {
        LOG.audit(JSON.toJSONString(result));
        if (!result.getCudResponse().isSuccess()) {
            DcInboundErrorInfo errorInfo = new DcInboundErrorInfo(cuo.getGraph(), cuo.getSchema(), DcInboundErrorInfo.ErrorType.RUNTIME_ERROR);
            errorInfo.setMsg(result.getCudResponse().getMessage());
            errorInfo.setInboundDataSuoInfo(cuo);
            errorInfo.setStoreType(storeType);
            monitorService.errorRecord(errorInfo);
        }
    }

    protected abstract DcInboundResult doProcessMessages(DcInboundDataSuo suo);

    protected void afterProcessMessages(DcInboundDataSuo suo, CudResponse cudResponse, StoreType storeType) {
        monitorService.metricStore(suo, cudResponse, storeType);
        LOG.audit("metric store after bulk insert data, taskInstanceId={0}, storeType:{1}",
                suo.getHeaderOptions().getString(DcConstants.KEY_TASK_INSTANCE_ID), storeType.getName());

        //increment error count if necessary
        if (cudResponse.getRowsErrors() > 0) {
            Long taskInstanceId = suo.getHeaderOptions().getLong(DcConstants.KEY_TASK_INSTANCE_ID);
            taskRedisService.incrementErrorCount(taskInstanceId, (long) cudResponse.getRowsErrors());
        }
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private DcInboundResult tryProcessMessages(DcInboundDataSuo cuo) {
        int retryCount = 0;
        DcInboundResult result = new DcInboundResult();
        do {
            try {
                result = doProcessMessages(cuo);
                if (result.isSuccess()) {
                    break;
                }
            } catch (Exception e) {
                if (retryCount >= MAX_RETRIES) {
                    LOG.error(e);
                } else {
                    LOG.warn(e);
                }
            }
            retryCount++;
        } while (retryCount <= MAX_RETRIES);
        result.setRetryCount(retryCount);
        resetResultIfNecessary(cuo, result, "happen error when bulk into " + storeType.getName());
        afterProcessMessages(cuo, result.getCudResponse(), storeType);
        return result;
    }

    private Schema getSchema(DcInboundDataSuo cuo) {
        Domain domain = dcMetadataCache.getDomain(cuo.getGraph());
        return domain.getSchema(cuo.getSchema());
    }

    private boolean needsPersist(Schema sch, StoreType storeType) {
        switch (storeType) {
            case Hbase:
                return sch.isUseHBase();
            case ES:
                return sch.isUseSearch();
            case GDB:
                return sch.isUseGraphDb();
        }
        return true;
    }

    private void resetResultIfNecessary(DcInboundDataSuo suo, DcInboundResult result, Object message) {
        if (Objects.isNull(result.getCudResponse())) {
            result.setCudResponse(new CudResponse());
        }
        CudResponse cudResponse = result.getCudResponse();
        if (!result.isSuccess()) {
            int errorRow = suo.getRowsSize() - cudResponse.getRowsIgnored() - cudResponse.getRowsAffected();
            cudResponse.setRowsErrors(errorRow > 0 ? errorRow : suo.getRowsSize());
            if (Objects.isNull(cudResponse.getMessage())) {
                cudResponse.setMessage(message);
            }
        }
    }

}
