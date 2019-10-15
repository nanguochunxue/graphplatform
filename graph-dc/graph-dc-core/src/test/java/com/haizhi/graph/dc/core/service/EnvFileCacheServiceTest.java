package com.haizhi.graph.dc.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by chengangxiong on 2019/04/17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class EnvFileCacheServiceTest {

    @Autowired
    private EnvFileCacheService envFileCacheService;

    @Test
    public void test(){
        Map<String, String> fileMap = envFileCacheService.findEnvFile(1L);
        System.out.println(fileMap);
        envFileCacheService.deleteEnvFile(1L, "krb5.conf");
        Map<String, String> fileMap2 = envFileCacheService.findEnvFile(1L);
        System.out.println(fileMap2);
    }
}