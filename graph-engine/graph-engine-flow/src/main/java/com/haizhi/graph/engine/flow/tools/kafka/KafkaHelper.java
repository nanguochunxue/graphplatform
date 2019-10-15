package com.haizhi.graph.engine.flow.tools.kafka;

import org.springframework.kafka.core.KafkaTemplate;

/**
 * Created by chengmo on 2018/3/13.
 */
public class KafkaHelper {

    private KafkaTemplate kafkaTemplate;

    public KafkaHelper(String locationConfig) {
        this.kafkaTemplate = new KafkaTemplate(KafkaConfig.producerFactory(locationConfig));
    }

    public KafkaTemplate kafkaTemplate() {
        return kafkaTemplate;
    }

    public void send(String topic, String message){
        kafkaTemplate.send(topic, message);
    }
}
