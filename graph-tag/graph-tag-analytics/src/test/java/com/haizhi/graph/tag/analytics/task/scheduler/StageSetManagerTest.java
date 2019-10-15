package com.haizhi.graph.tag.analytics.task.scheduler;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.service.DcMetadataService;
import com.haizhi.graph.tag.analytics.task.context.DomainFactory;
import com.haizhi.graph.tag.core.bean.TagDomain;
import com.haizhi.graph.tag.core.service.TagMetadataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

/**
 * Created by chengmo on 2018/4/8.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "tag")
public class StageSetManagerTest {

    static final String GRAPH = "crm_dev2";

    @Autowired
    TagMetadataService tagService;
    @Autowired
    DcMetadataService dcMetadataService;

    @Test
    public void get() {
        TagDomain tagDomain = tagService.getTagDomain(GRAPH);
        Domain domain = DomainFactory.createOnHive(GRAPH);
        //Domain domain = metadataService.getDomain(GRAPH);

        StageSet stageSet = StageSetManager.get(tagDomain, domain);
        System.out.println(JSON.toJSONString(stageSet, true));
    }

    @Test
    public void getByTagIds() {
        TagDomain tagDomain = tagService.getTagDomain(GRAPH);
        Domain domain = DomainFactory.createOnHive(GRAPH);
        //Domain domain = metadataService.getDomain(GRAPH);
        //Set<Long> tagIds = Sets.newHashSet(5L, 6L, 7L);
        Set<Long> tagIds = Sets.newHashSet(405001L, 405002L, 405003L);
        StageSet stageSet = StageSetManager.get(tagIds, tagDomain, domain);
        System.out.println(JSON.toJSONString(stageSet, true));
    }
}
