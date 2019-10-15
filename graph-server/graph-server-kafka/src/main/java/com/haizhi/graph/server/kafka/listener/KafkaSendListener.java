package com.haizhi.graph.server.kafka.listener;

/**
 * Created by chengmo on 2018/10/24.
 */
public interface KafkaSendListener {

    void onSuccess();

    void onFailure(Throwable ex);
}
