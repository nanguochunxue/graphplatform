package com.haizhi.graph.server.kafka.service;

import com.haizhi.graph.server.kafka.listener.KafkaSendListener;
import org.springframework.kafka.support.SendResult;

/**
 * Created by chengmo on 2018/10/24.
 */
public interface KafkaService {

    /**
     * Synchronous send.
     *
     * @param topic
     * @param message
     * @return
     */
    boolean send(String topic, String message);

    void asyncSend(String topic, String message, KafkaSendListener callback);

    /**
     * Asynchronous send.
     *
     * @param message
     * @param callback
     * @return
     */
    void asyncSend(String topic, String schema, String message, KafkaSendListener callback);

    SendResult<String,String> syncSend(String topic, String message) throws Exception;
}
