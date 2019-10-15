package com.haizhi.graph.dc.inbound.engine.driver;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.dc.core.constant.TaskType;
import com.haizhi.graph.dc.inbound.engine.conf.DcFlowTask;
import org.junit.Test;

/**
 * Created by chengmo on 2019/2/12.
 */
public class SparkExtractDriverTest {

    @Test
    public void runLocallyOnHDFS() throws Exception {
        DcFlowTask task = new DcFlowTask();
        task.setDebugEnabled(true);
        task.setTaskType(TaskType.HDFS);
        task.setGraph("graph_hello");
        task.setSchema("company");
        task.setOperation(GOperation.CREATE_OR_UPDATE);
        task.setTaskId(8L);
        task.setInstanceId(534L);
        //task.setSource("hdfs://hadoop01.sz.haizhi.com:8022/user/graph/dc/data/dc_inbound.json");
        task.setSource(Lists.newArrayList("/user/graph/task_0b66def2-41f7-464e-9341-f7c9fc3cd3a7.json"));
        task.setInboundApiUrl("http://localhost:10010/dc/inbound/api/bulk");
        SparkExtractDriver.main(new String[]{JSON.toJSONString(task)});
    }

    @Test
    public void runLocallyOnHive() throws Exception {
        DcFlowTask task = new DcFlowTask();
        task.setDebugEnabled(true);
        task.setRunLocallyEnabled(true);
        task.setTaskType(TaskType.HIVE);
        task.setSource(Lists.newArrayList("select * from crm_dev2.to_account"));
        // select * from web_logs limit 20
        task.setInboundApiUrl("http://10.10.10.7:10010/dc/inbound/api/bulk");
        SparkExtractDriver.main(new String[]{JSON.toJSONString(task)});
    }
}
