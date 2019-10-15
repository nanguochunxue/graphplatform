package com.haizhi.graph.server.kafka.service;

import com.haizhi.graph.server.kafka.listener.KafkaSendListener;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengmo on 2018/10/24.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi")
public class KafkaServiceTest {

    @Autowired
    KafkaService kafkaService;

    @Test
    public void send() {
        boolean success = kafkaService.send("example-metric1", "test message");
        Assert.assertTrue(success);
    }

    @Test
    public void asyncSend() {
        String topic = "example-metric1";
        String key = "tv_user";
        String value = "test meassage";
        kafkaService.asyncSend(topic, key, value, new KafkaSendListener() {
            @Override
            public void onSuccess() {
                System.out.println("success");
            }

            @Override
            public void onFailure(Throwable ex) {
                System.out.println("failure");
            }
        });
    }
}
