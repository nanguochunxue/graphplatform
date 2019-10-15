package com.haizhi.graph.engine.flow.tools.kafka;

import org.junit.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.ExecutionException;

/**
 * Created by chengmo on 2018/3/13.
 */
public class KafkaHelperTest {

    @Test
    public void test() throws ExecutionException, InterruptedException {
        String profile = "application-test.properties";
        //String profile = Resource.getActiveProfile();
        KafkaHelper helper = new KafkaHelper(profile);
        KafkaTemplate<String, String> kafkaTemplate = helper.kafkaTemplate();
        SendResult<String, String> result = kafkaTemplate.send("tag-topic", "test1").get();
        System.out.println(result.getProducerRecord());
    }
}
