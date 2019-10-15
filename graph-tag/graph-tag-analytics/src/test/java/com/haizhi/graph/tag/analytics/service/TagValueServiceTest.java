package com.haizhi.graph.tag.analytics.service;

import com.google.common.collect.Sets;
import com.haizhi.graph.common.bean.Result;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.tag.analytics.model.TagValueReq;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengmo on 2018/7/27.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class TagValueServiceTest {

    static final String GRAPH = "crm_dev2";

    @Autowired
    TagValueService tagValueService;

    @Test
    public void bulkUpsert(){
        TagValueReq req = new TagValueReq();
        req.setGraph(GRAPH);
        req.setTagId(1002L);
        req.setObjectKeys(Sets.newHashSet("18e032203a060ff1a53eb39e636892d8"));
        Result result = tagValueService.bulkUpsert(req);
        JSONUtils.println(result);
    }
}
