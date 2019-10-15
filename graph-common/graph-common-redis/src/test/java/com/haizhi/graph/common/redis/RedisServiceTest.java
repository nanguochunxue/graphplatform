package com.haizhi.graph.common.redis;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.redis.key.RKeys;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liulu on 2019/6/10.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    private final static String key = "redisServiceTest";

    @Test
    public void setAndGetTest(){
        Map<String,Object> map = new HashMap<>();
        map.put("name","XiaoLi");
        map.put("age",30);
        redisService.put(key,map);
        if (!redisService.hasKey(key)){
            System.out.println(String.format("key:%s not exist",key));
            return;
        }
        Map<String,Object> result = redisService.get(key);
        System.out.println(String.format("Get redis by key:%s",key));
        System.out.println(JSON.toJSONString(result,true));
    }

    @Test
    public void deleteTest(){
        redisService.delete(key);
        System.out.println(String.format("Deleted redis by key:%s",key));
    }

    @Test
    public void delete(){
        redisService.deleteByPattern("com:haizhi.*");
        redisService.deleteByPattern(RKeys.SSO_SESSION);
        System.out.println(String.format("Deleted redis by key:%s",key));
    }
}
