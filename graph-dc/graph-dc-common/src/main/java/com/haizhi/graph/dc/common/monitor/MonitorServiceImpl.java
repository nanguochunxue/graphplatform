package com.haizhi.graph.dc.common.monitor;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.common.model.DcInboundErrorInfo;
import com.haizhi.graph.dc.common.model.DcInboundTaskMetric;
import com.haizhi.graph.server.kafka.listener.KafkaSendListener;
import com.haizhi.graph.server.kafka.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by chengangxiong on 2019/03/12
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    private static final GLog LOG = LogFactory.getLogger(MonitorServiceImpl.class);

    @Autowired
    private KafkaService kafkaService;

    @Value("${graph.dc.inbound.metric.topic}")
    private String metricTopic;

    @Value("${graph.dc.inbound.error.topic}")
    private String errorTopic;

    @Override
    public void metricStore(DcInboundDataSuo suo, CudResponse cudResponse, StoreType storeType) {
        try {
            DcInboundTaskMetric metric = new DcInboundTaskMetric(suo.getHeaderOptions());
            metric.setRows(Long.valueOf(cudResponse.getRowsAffected()));
            metric.setErrorRows(Long.valueOf(cudResponse.getRowsErrors()));
            metric.setStoreType(storeType);
            doSend(metric);
        } catch (Exception e) {
            LOG.warn("send metric got exception", e);
        }
    }

    @Override
    public void metricTotal(DcInboundDataSuo suo) {
        try {
            DcInboundTaskMetric metric = new DcInboundTaskMetric(suo.getHeaderOptions());
            metric.setRows(suo.getRows() == null ? 0 : Long.valueOf(suo.getRows().size()));
            doSend(metric);
        } catch (Exception e) {
            LOG.warn("send metric got exception", e);
        }
    }

    private void doSend(Object obj) {
        kafkaService.send(metricTopic, JSON.toJSONString(obj));
    }

    @Override
    public void errorRecord(DcInboundErrorInfo errorInfo) {
        kafkaService.asyncSend(errorTopic, JSON.toJSONString(errorInfo), new KafkaSendListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(Throwable ex) {
                LOG.error("send error info to kafka got exception", ex);
            }
        });
    }
}
