package com.haizhi.graph.dc.es.service;

import com.haizhi.graph.dc.common.model.DcInboundErrorInfo;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

public interface ErrorInfoService {

    void recordInfo(List<ConsumerRecord<String, String>> records);

    void recordInfo(ConsumerRecord<String, String> record);

    boolean doRecord(DcInboundErrorInfo errorInfo);
}
