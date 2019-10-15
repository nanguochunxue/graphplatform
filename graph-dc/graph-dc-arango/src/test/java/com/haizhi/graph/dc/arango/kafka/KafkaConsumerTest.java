package com.haizhi.graph.dc.arango.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * Created by chengmo on 2017/11/02.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi-fi")
public class KafkaConsumerTest {

    @KafkaListener(topics = "example-metric1")
    public void processMessage(ConsumerRecord record) {
        System.out.println("Success to receive: " + record);
    }

    @Test
    public void receiveMassage() throws ExecutionException, InterruptedException {
        System.out.println("Receive Massage starting...");
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }

}
