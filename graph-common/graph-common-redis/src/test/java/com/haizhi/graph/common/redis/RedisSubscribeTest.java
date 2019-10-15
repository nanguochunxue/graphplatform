package com.haizhi.graph.common.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

/**
 * Created by chengmo on 2019/6/24.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class RedisSubscribeTest {

    @Autowired
    DemoSubListener demoSubListener;

    @Test
    public void subscribe() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}
