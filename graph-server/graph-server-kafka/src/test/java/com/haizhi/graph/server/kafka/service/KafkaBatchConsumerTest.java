package com.haizhi.graph.server.kafka.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * Created by chengmo on 2017/11/02.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi-ksyun")
public class KafkaBatchConsumerTest {

    @KafkaListener(topics = KafkaProducerTest.TOPIC, containerFactory = "batchFactory")
    public void listen(List<ConsumerRecord<?, ?>> records, Acknowledgment ack) {
        for (ConsumerRecord<?, ?> record : records) {
            System.out.println("Success to receive: " + record);
        }
        ack.acknowledge();
    }

    @Test
    public void receiveMassage() throws ExecutionException, InterruptedException {
        System.out.println("Receive Massage starting...");
        CountDownLatch cdl = new CountDownLatch(1);
        cdl.await();
    }

}
