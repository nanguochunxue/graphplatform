package com.haizhi.graph.server.kafka.service.impl;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.kafka.listener.KafkaSendListener;
import com.haizhi.graph.server.kafka.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;

/**
 * Created by chengmo on 2018/10/24.
 */
@Service
public class KafkaServiceImpl implements KafkaService {
    private static final GLog LOG = LogFactory.getLogger(KafkaServiceImpl.class);

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Override
    public boolean send(String topic, String message) {
        boolean success = false;
        try {
            kafkaTemplate.send(topic, message).get();
            success = true;
        } catch (InterruptedException e) {
            LOG.error(e);
        } catch (ExecutionException e) {
            LOG.error(e);
        }
        return success;
    }

    @Override
    public void asyncSend(String topic, String message, KafkaSendListener callback) {
        kafkaTemplate.send(topic, message).addCallback(new ListenableFutureCallback() {
            @Override
            public void onFailure(Throwable ex) {
                callback.onFailure(ex);
            }

            @Override
            public void onSuccess(Object result) {
                callback.onSuccess();
            }
        });
    }

    @Override
    public void asyncSend(String topic, String schema, String message, KafkaSendListener callback) {
        try {
            kafkaTemplate.send(topic, schema, message).addCallback(new ListenableFutureCallback() {
                @Override
                public void onSuccess(Object result) {
                    callback.onSuccess();
                }

                @Override
                public void onFailure(Throwable ex) {
                    callback.onFailure(ex);
                }
            });
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    @Override
    public SendResult<String,String> syncSend(String topic, String message) throws Exception {
        SendResult<String,String> res = (SendResult<String, String>) kafkaTemplate.send(topic, message).get();
        return res;
    }
}
