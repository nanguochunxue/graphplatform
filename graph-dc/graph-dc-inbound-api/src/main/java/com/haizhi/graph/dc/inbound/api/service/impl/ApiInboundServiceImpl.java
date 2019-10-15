package com.haizhi.graph.dc.inbound.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.v1.Response;
import com.haizhi.graph.dc.core.constant.DcConstants;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.common.model.DcInboundErrorInfo;
import com.haizhi.graph.dc.common.monitor.MonitorService;
import com.haizhi.graph.dc.common.util.TopicGetter;
import com.haizhi.graph.dc.inbound.api.service.ApiInboundService;
import com.haizhi.graph.dc.inbound.api.validator.AbstractValidator;
import com.haizhi.graph.dc.inbound.api.validator.ApiInboundValidator;
import com.haizhi.graph.dc.inbound.api.validator.ValidatorBuilder;
import com.haizhi.graph.dc.inbound.api.validator.ValidatorResult;
import com.haizhi.graph.server.kafka.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * Created by chengmo on 2018/10/24.
 */
@Service
public class ApiInboundServiceImpl implements ApiInboundService {

    private static final GLog LOG = LogFactory.getLogger(ApiInboundServiceImpl.class);

    private static AbstractValidator validateHandler;

    @Autowired
    private KafkaService kafkaService;

    @Value("${graph.dc.inbound.data.topic.prefix}")
    private String topicPrefix;

    @Autowired
    private MonitorService monitorService;

    @PostConstruct
    public void initialize() {
        validateHandler = ValidatorBuilder.build()
                .add(ApiInboundValidator.class)
                .get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response bulkInbound(DcInboundDataSuo suo) {
        Response response = Response.error();
        ValidatorResult result = new ValidatorResult(100);
        try {
//            LOG.audit("DcInboundDataSuo: {0}", JSON.toJSONString(suo,true));
            // graph not found | totalRows=100,errorRows=100
            if (!validateHandler.validate(suo, result)) {
                recordErrorInfo(suo, result, response);
                return response;
            }
            // suo.rows=90
            this.syncSendMessage(suo);
            // totalRows=100,errorRows=10
            if (result.hasErrorRow() || result.hasErrorMsg()) {
                recordErrorInfo(suo, result, response);
            }
            response.setSuccess(!result.hasErrorRow());
        } catch (Exception e) {
            LOG.error(e);
            response.setMessage(result.getAllErrorMsg());
            result.addErrorRows(suo.getRows());
            result.addErrorMsg(e.getMessage());
            sendRuntimeErrorInfo(suo, result);
        }
        return response;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private void recordErrorInfo(DcInboundDataSuo suo, ValidatorResult result, Response response) {
        response.setMessage(result.getAllErrorMsg());
        response.setPayload(result.getErrorRows().size());
        DcInboundErrorInfo errorInfo = new DcInboundErrorInfo(suo.getGraph(), suo.getSchema(),
                DcInboundErrorInfo.ErrorType.CHECK_ERROR);
        errorInfo.setMsg(result.getBuffer().getLines());
        errorInfo.setInboundDataSuoInfo(suo, result.getErrorRows());
        monitorService.errorRecord(errorInfo);
        LOG.audit("valid false, graph={0}, schema={1}, errorMsg={2}",
                suo.getGraph(), suo.getSchema(), result.getAllErrorMsg());
    }

    private void syncSendMessage(DcInboundDataSuo cuo) throws Exception {
        String graph = cuo.getGraph();
        String schema = cuo.getSchema();
        String topic = TopicGetter.get(topicPrefix, graph, schema);
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
        String message = JSON.toJSONString(cuo, serializeConfig);
        String taskId = cuo.getHeaderOptions().getString(DcConstants.KEY_TASK_ID);
        LOG.audit("send data to kafka, topic={0}, taskId={1}, data size={2}",
                topic, taskId, cuo.getRowsSize());
        kafkaService.syncSend(topic, message);
    }

    private void sendRuntimeErrorInfo(DcInboundDataSuo cuo, ValidatorResult result) {
        try {
            DcInboundErrorInfo errorInfo = new DcInboundErrorInfo(cuo.getGraph(),
                    cuo.getSchema(), DcInboundErrorInfo.ErrorType.RUNTIME_ERROR);
            errorInfo.setInboundDataSuoInfo(cuo, result.getErrorRows());
            errorInfo.setMsg(result.getAllErrorMsg());
            monitorService.errorRecord(errorInfo);
        } catch (Exception e1) {
            LOG.warn("send runtime error got exception", e1);
        }
    }
}