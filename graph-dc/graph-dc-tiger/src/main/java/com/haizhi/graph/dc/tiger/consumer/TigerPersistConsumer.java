package com.haizhi.graph.dc.tiger.consumer;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.dc.common.consumer.AbstractPersistConsumer;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.common.model.DcInboundResult;
import com.haizhi.graph.dc.tiger.service.TigerPersistService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by tanghaiyang on 2018/11/14.
 */
@Component
public class TigerPersistConsumer extends AbstractPersistConsumer {
    private static final GLog log = LogFactory.getLogger(TigerPersistConsumer.class);

    @Autowired
    private TigerPersistService tigerPersistService;

    @PostConstruct
    protected void setStoreType() {
        storeType = StoreType.GDB;
    }

    @KafkaListener(topicPattern = "${graph.dc.inbound.data.topic.prefix}.*", containerFactory = "batchFactory")
    public void listen(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        log.info("tiger inbound consume data, size:" + records.size());
        super.processMessages(records, ack, storeType);
    }

    @Override
    protected DcInboundResult doProcessMessages(DcInboundDataSuo cuo) {
        CudResponse cudResponse = tigerPersistService.bulkPersist(cuo);
        return DcInboundResult.get(cudResponse);
    }

}
