package com.haizhi.graph.dc.es.consumer;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.dc.common.consumer.AbstractPersistConsumer;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.common.model.DcInboundResult;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.dc.es.service.EsPersistService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by chengmo on 2018/11/14.
 */
@Component
public class EsPersistConsumer extends AbstractPersistConsumer {
    private static final GLog LOG = LogFactory.getLogger(EsPersistConsumer.class);

    @Autowired
    private EsPersistService esPersistService;

    @PostConstruct
    protected void setStoreType() {
        storeType = StoreType.ES;
    }

    @KafkaListener(topicPattern = "${graph.dc.inbound.data.topic.prefix}.*", containerFactory = "batchFactory")
    public void listen(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        super.processMessages(records, ack, storeType);
    }

    @Override
    protected DcInboundResult doProcessMessages(DcInboundDataSuo cuo) {
        CudResponse cudResponse = esPersistService.bulkPersist(cuo);
        LOG.audit("cudResponse:\n{0}", JSON.toJSONString(cudResponse, true));
        return DcInboundResult.get(cudResponse);
    }
}
