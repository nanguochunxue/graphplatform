package com.haizhi.graph.tag.analytics.task.scheduler;

import com.google.common.collect.Sets;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.tag.analytics.bean.TagContext;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTask;
import com.haizhi.graph.tag.analytics.task.DagTaskExecutor;
import com.haizhi.graph.tag.analytics.task.TaskExecutor;
import com.haizhi.graph.tag.analytics.task.context.DomainFactory;
import com.haizhi.graph.tag.analytics.task.context.TaskContext;
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
 * Created by chengmo on 2018/7/11.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "tag")
public class FlowTaskBuilderTest {

    @Autowired
    TagMetadataService tagService;

    @Test
    public void buildFlowTask() {
        String graphName = "crm_dev2";
        TaskExecutor taskExecutor = new DagTaskExecutor();
        TagContext ctx = taskExecutor.createTagContext(new TaskContext(graphName));
        TagDomain tagDomain = tagService.getTagDomain(graphName);
        Domain domain = DomainFactory.createOnHive(graphName);

        // task
        Set<Long> tagIds = Sets.newHashSet(706001L);
        StageSet stageSet = StageSetManager.get(tagIds, tagDomain, domain);
        Stage.Task task = stageSet.getTasks().values().iterator().next();

        FlowTask flowTask = FlowTaskBuilder.build(task, ctx);
        JSONUtils.println(flowTask);
    }
}
