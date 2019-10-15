package com.haizhi.graph.tag.analytics.task.context;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.dc.core.bean.Domain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengmo on 2018/4/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class DomainFactoryTest {

    @Test
    public void createOnHive(){
        String graphName = "default";
        Domain hiveDomain = DomainFactory.createOnHive(graphName);
        System.out.println(JSON.toJSONString(hiveDomain, true));
    }
}
