package com.haizhi.graph.api.service;

import com.haizhi.graph.sys.auth.service.SysUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

/**
 * Create by zhoumingbing on 2019-07-04
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi-fi")
public class SysUserServiceTest {

    @Autowired
    private SysUserService sysUserService;

    @Test
    public void getUserResourceURL() {
        Long userNo = 1L;
        Set<String> userResourceURL = sysUserService.getUserResourceURL(userNo);
        userResourceURL.forEach(url -> System.out.println(url));
    }
}
