package com.haizhi.graph.common.redis;

/**
 * Created by chengmo on 2019/6/24.
 */
public interface RedisSubListener {

    void receiveMessage(String message, String channel);
}
