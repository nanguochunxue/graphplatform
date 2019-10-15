package com.haizhi.graph.tag.analytics.engine.driver;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.service.DcMetadataService;
import com.haizhi.graph.engine.flow.tools.hdfs.HDFSHelper;
import com.haizhi.graph.tag.analytics.bean.TagContext;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTask;
import com.haizhi.graph.tag.analytics.task.DagTaskExecutor;
import com.haizhi.graph.tag.analytics.task.TaskExecutor;
import com.haizhi.graph.tag.analytics.task.context.DomainFactory;
import com.haizhi.graph.tag.analytics.task.context.TaskContext;
import com.haizhi.graph.tag.analytics.task.scheduler.FlowTaskBuilder;
import com.haizhi.graph.tag.analytics.task.scheduler.Stage;
import com.haizhi.graph.tag.analytics.task.scheduler.StageSet;
import com.haizhi.graph.tag.analytics.task.scheduler.StageSetManager;
import com.haizhi.graph.tag.analytics.util.Constants;
import com.haizhi.graph.tag.core.bean.TagDomain;
import com.haizhi.graph.tag.core.service.TagMetadataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.MessageFormat;
import java.util.Set;

/**
 * Created by chengmo on 2018/4/4.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "tag")
public class LogicSparkOnHBaseDriverTest {

    @Autowired
    TagMetadataService tagService;
    @Autowired
    DcMetadataService dcMetadataService;

    @Test
    public void run() throws Exception {
        String graphName = "crm_dev2";
        TaskExecutor taskExecutor = new DagTaskExecutor();
        TagContext ctx = taskExecutor.createTagContext(new TaskContext(graphName));
        ctx.setTaskIdPrefix("TAG.DAG");
        TagDomain tagDomain = tagService.getTagDomain(graphName);
        Domain domain = DomainFactory.createOnHive(graphName);

        // task
        Set<Long> tagIds = Sets.newHashSet(701001L);
        StageSet stageSet = StageSetManager.get(tagIds, tagDomain, domain);
        Stage.Task task = stageSet.getTasks().values().iterator().next();

        FlowTask flowTask = FlowTaskBuilder.build(task, ctx);
        flowTask.setDebugEnabled(true);
        System.out.println(JSON.toJSONString(flowTask));
        flowTask = JSON.parseObject(JSON.toJSONString(flowTask), FlowTask.class);

        // upload to hdfs
        String flowTaskId = flowTask.getId();
        String path = MessageFormat.format(Constants.PATH_FLOW_TASK, flowTaskId);
        HDFSHelper hdfsHelper = new HDFSHelper();
        hdfsHelper.upsertLine(path, JSON.toJSONString(flowTask));
        String[] args = new String[]{path};
        LogicSparkOnHBaseDriver.main(args);
    }
}
