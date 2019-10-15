package com.haizhi.graph.server.kafka.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

/**
 * Created by chengmo on 2018/3/19.
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.consumer.batch.listener:false}")
    private boolean batchListener;

    @Value("${spring.kafka.consumer.concurrency:1}")
    private int concurrency;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> batchFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new
                ConcurrentKafkaListenerContainerFactory<>();
        // 设置为批量消费，每个批次数量在Kafka配置参数中设置ConsumerConfig.MAX_POLL_RECORDS_CONFIG
        factory.setBatchListener(batchListener);
        // 启动时，会创建{concurrency}个线程，每个线程起一个Consumer
        factory.setConcurrency(concurrency);
        configurer.configure(factory, kafkaConsumerFactory);
        return factory;
    }
}
