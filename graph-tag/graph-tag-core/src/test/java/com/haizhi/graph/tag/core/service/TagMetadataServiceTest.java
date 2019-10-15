package com.haizhi.graph.tag.core.service;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.tag.core.bean.TagDomain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengmo on 2018/3/12.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "tag")
public class TagMetadataServiceTest {

    @Autowired
    TagMetadataService tagService;

    @Test
    public void getTagDomain(){
        TagDomain domain = tagService.getTagDomain("crm_dev2");
        System.out.println(domain.getTagInfoMap().size());
        String json = JSON.toJSONString(domain);
        TagDomain tagDomain = JSON.parseObject(json, TagDomain.class);
        System.out.println(JSON.toJSONString(tagDomain, true));
    }
}
