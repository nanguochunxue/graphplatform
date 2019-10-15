package com.haizhi.graph.common.redis;

import com.haizhi.graph.common.redis.channel.ChannelKeys;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengmo on 2019/6/24.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class RedisPublishTest {

    @Autowired
    RedisService redisService;

    @Test
    public void publish() {
        redisService.publish(ChannelKeys.DC_METADATA, "changed");
    }
}
