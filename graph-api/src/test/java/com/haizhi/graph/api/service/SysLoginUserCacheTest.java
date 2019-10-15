package com.haizhi.graph.api.service;

import com.haizhi.graph.sys.auth.shiro.redis.SysLoginUserPubService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Create by zhoumingbing on 2019-07-05
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class SysLoginUserCacheTest {

    @Autowired
    private SysLoginUserPubService sysLoginUserPubService;

    @Test
    public void publishByUserId() {
        Long userId = 1000001L;
        sysLoginUserPubService.publishByUserId(userId);
    }

    @Test
    public void publishByRoleId() {
        Long roleId = 6L;
        sysLoginUserPubService.publishByRoleId(roleId);
    }
}
