package com.haizhi.graph.common.redis;

import com.haizhi.graph.common.redis.channel.ChannelKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by chengmo on 2019/6/24.
 */
@Component
public class DemoSubListener implements RedisSubListener {

    @Autowired
    RedisSubContainer redisSubContainer;

    @PostConstruct
    public void init(){
        redisSubContainer.addSubListener(ChannelKeys.DC_METADATA, this);
    }

    @Override
    public void receiveMessage(String message, String channel) {
        System.out.println("message=" + message);
        System.out.println("channel=" + channel);
    }
}
