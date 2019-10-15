package com.haizhi.graph.api.kafka;

import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by chengmo on 2017/11/02.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi-ksyun")
public class KafkaProducerTest {

    static final String TOPIC = "test.example-metric1";

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Test
    public void sendMassage() throws ExecutionException, InterruptedException {
        for (int i = 0; i < 2; i++) {
            SendResult<String, String> result = kafkaTemplate.send(TOPIC, "test message" + i).get();
            System.out.println(result.getProducerRecord());
            System.out.println(result.getRecordMetadata());
            Thread.sleep(2000);
        }
    }

    @Test
    public void partitionsFor(){
        List<PartitionInfo> infoList = kafkaTemplate.partitionsFor(TOPIC);
        infoList.forEach(e -> {
            System.out.println(e);
        });
        Map<MetricName, ? extends Metric> map = kafkaTemplate.metrics();
        map.forEach((k, v) -> {
            System.out.println(k);
            System.out.println(v);
        });
    }
}
