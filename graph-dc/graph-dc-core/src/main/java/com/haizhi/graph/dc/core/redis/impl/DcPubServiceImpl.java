package com.haizhi.graph.dc.core.redis.impl;

import com.haizhi.graph.common.redis.RedisService;
import com.haizhi.graph.common.redis.channel.ChannelKeys;
import com.haizhi.graph.dc.core.redis.DcPubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by chengmo on 2019/6/25.
 */
@Service
public class DcPubServiceImpl implements DcPubService {

    @Autowired
    private RedisService redisService;

    @Override
    public boolean publish(String message) {
        return redisService.publish(ChannelKeys.DC_METADATA, message);
    }
}
