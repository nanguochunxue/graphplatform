package com.haizhi.graph.tag.analytics.service;

import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.model.v0.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengmo on 2018/8/9.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class TagPersistServiceTest {

    @Autowired
    TagPersistService tagPersistService;

    @Test
    public void syncDataToEs() {
        Response response = tagPersistService.syncDataToEs("crm_dev2");
        JSONUtils.println(response);
    }
}
