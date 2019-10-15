package com.haizhi.graph.tag.analytics.engine.result;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * Created by chengmo on 2018/8/2.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "tag-hdp")
public class KafkaConsumerTest {

    @KafkaListener(topics = "${tag.analytics.kafka.topic}", containerFactory = "batchFactory")
    public void listen(List<ConsumerRecord<?, ?>> records, Acknowledgment ack){
        for (ConsumerRecord<?, ?> record : records) {
            String message = Objects.toString(record.value());
            System.out.println(message);
        }
    }

    @Test
    public void receiveMassage() throws ExecutionException, InterruptedException {
        System.out.println("Receive Massage starting...");
        CountDownLatch cdl = new CountDownLatch(1);
        cdl.await();
    }
}
