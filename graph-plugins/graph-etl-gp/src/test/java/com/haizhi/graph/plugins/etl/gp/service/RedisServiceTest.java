package com.haizhi.graph.plugins.etl.gp.service;

import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.redis.RedisService;
import com.haizhi.graph.common.redis.key.RKeys;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengmo on 2019/6/13.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi-remote")
public class RedisServiceTest {

    @Autowired
    RedisService redisService;

    @Test
    public void get(){
        String key = RKeys.DC_TASK_GRAPH_ETL_GP + ":-1";
        Response response = redisService.get(key);
        JSONUtils.println(response);
        key = RKeys.DC_TASK_GRAPH_ETL_GP + ":0";
        response = redisService.get(key);
        JSONUtils.println(response);
    }

    @Test
    public void delete(){
        String key = RKeys.DC_TASK_GRAPH_ETL_GP + ":-1";
        System.out.println(redisService.delete(key));
        key = RKeys.DC_TASK_GRAPH_ETL_GP + ":0";
        System.out.println(redisService.delete(key));
    }

    @Test
    public void put(){
        String key = RKeys.DC_TASK_GRAPH_ETL_GP + ":-1";

    }
}
