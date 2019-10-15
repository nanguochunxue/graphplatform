package com.haizhi.graph.dc.inbound.engine;

import com.google.common.collect.Lists;
import com.haizhi.graph.dc.core.constant.TaskType;
import com.haizhi.graph.dc.inbound.engine.conf.DcFlowTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengmo on 2019/2/21.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class JobRunnerTest {

    @Autowired
    private JobRunner jobRunner;

    @Test
    public void runLocalExample(){
        DcFlowTask task = new DcFlowTask();
        task.setId("DC.EXTRACT=>hdfs.task_1_1");
        task.setRunLocallyEnabled(false);
        task.setTaskType(TaskType.HDFS);
        //task.setSource("hdfs://hadoop01.sz.haizhi.com:8022/user/graph/dc/data/dc_inbound.json");
        task.setInboundApiUrl("http://10.10.10.7:10010/dc/inbound/api/bulk");
        task.setSource(Lists.newArrayList("/user/graph/dc/data/dc_inbound.json"));
        jobRunner.waitForCompletion(task);
    }

    @Test
    public void runRemoteExample(){
        DcFlowTask task = new DcFlowTask();
        task.setId("DC.EXTRACT=>hdfs.task_1_1");
        task.setRunLocallyEnabled(true);
        task.setTaskType(TaskType.HDFS);
        //task.setSource("hdfs://hadoop01.sz.haizhi.com:8022/user/graph/dc/data/dc_inbound.json");
        task.setInboundApiUrl("http://10.10.10.7:10010/dc/inbound/api/bulk");
        task.setSource(Lists.newArrayList("/user/graph/dc/data/dc_inbound.json"));
        jobRunner.waitForCompletion(task);
    }

    @Test
    public void test2(){
        DcFlowTask dcFlowTask = new DcFlowTask();
        // DcFlowTask(id=DC.EXTRACT=>HDFS.task_18_112, graph=null, taskType=HDFS, source=[/user/graph/task_d59d76d9-55b5-4c47-9894-64741604acef.json], inboundApiUrl=http://localhost:10010/dc/inbound/api/bulk)
        dcFlowTask.setId("DC.EXTRACT=>HDFS.task_19_1");
        dcFlowTask.setGraph("graph");
//        dcFlowTask.setDebugEnabled(true);
        dcFlowTask.setRunLocallyEnabled(false);
        dcFlowTask.setTaskType(TaskType.HDFS);
        dcFlowTask.setSource(Lists.newArrayList("/user/graph/task_d59d76d9-55b5-4c47-9894-64741604acef.json"));
        dcFlowTask.setInboundApiUrl("http://localhost:10010/dc/inbound/api/bulk");

        jobRunner.waitForCompletion(dcFlowTask);
    }

    @Test
    public void testHive(){
        DcFlowTask dcFlowTask = new DcFlowTask();
        // DcFlowTask(id=DC.EXTRACT=>HDFS.task_18_112, graph=null, taskType=HDFS, source=[/user/graph/task_d59d76d9-55b5-4c47-9894-64741604acef.json], inboundApiUrl=http://localhost:10010/dc/inbound/api/bulk)
        dcFlowTask.setId("DC.EXTRACT=>HDFS.task_19_33");
        dcFlowTask.setGraph("graph");
        dcFlowTask.setDebugEnabled(true);
        dcFlowTask.setRunLocallyEnabled(true);
        dcFlowTask.setTaskType(TaskType.HIVE);
        dcFlowTask.setSource(Lists.newArrayList("select * from crm_dev2.to_account"));
        dcFlowTask.setInboundApiUrl("http://localhost:10010/dc/inbound/api/bulk");

        jobRunner.waitForCompletion(dcFlowTask);
    }
}
