package com.haizhi.graph.dc.inbound.service;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.dc.core.constant.ExecutionType;
import com.haizhi.graph.dc.core.constant.TaskType;
import com.haizhi.graph.dc.core.model.qo.DcTaskQo;
import com.haizhi.graph.dc.core.model.suo.DcTaskSuo;
import com.haizhi.graph.dc.core.model.vo.DcTaskPageVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

/**
 * Created by chengangxiong on 2019/02/14
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi-fi")
public class TaskInboundServiceTest {

    @Autowired
    private TaskInboundService taskInboundService;

    @Test
    public void findTaskPage() {
        DcTaskQo taskQo = new DcTaskQo();
        taskQo.setGraph("demo_graph");
        taskQo.getPage().setPageSize(10);
        PageResponse<DcTaskPageVo> response = taskInboundService.findTaskPage(taskQo);
        JSONUtils.println(response);
    }

    @Test
    public void create() throws InterruptedException {
        DcTaskSuo dcTaskSuo = newTestSuo();
        taskInboundService.createOrUpdate(dcTaskSuo);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }

    private DcTaskSuo newTestSuo() {
        DcTaskSuo dcTaskSuo = new DcTaskSuo();
        dcTaskSuo.setTaskName("test-aaa");
        dcTaskSuo.setTaskType(TaskType.HDFS);
        dcTaskSuo.setStoreId(33L);
        dcTaskSuo.setRunImmediately(true);
        dcTaskSuo.setExecutionType(ExecutionType.CRON);
        dcTaskSuo.setCron("0/10 * * * * ? ");
        dcTaskSuo.setGraph("test-graph");
        dcTaskSuo.setSchema("test-schema");
        dcTaskSuo.setSource("hdfs:/localhost:50700/usr/local/data/a.txt");
        return dcTaskSuo;
    }

    @Test
    public void delete() throws InterruptedException {

        Long taskId = 14L;
        taskInboundService.submit(null);
        Thread.sleep(5 * 60 * 1000L);
        taskInboundService.delete(taskId);
    }

    @Test
    public void start() throws InterruptedException {
        DcTaskSuo suo = newTestSuo();
        taskInboundService.createOrUpdate(suo);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }

    @Test
    public void stop() throws InterruptedException {

        Long taskId = 15L;
        taskInboundService.submit(null);
        Thread.sleep(17 * 1000L);
        taskInboundService.stop(taskId);
        Thread.sleep(7 * 1000L);
        taskInboundService.submit(null);
        Thread.sleep(25 * 1000L);
    }

    @Test
    public void checkGraphUpdate() {
        Long graphId = 1L;
        System.out.println(JSON.toJSONString(taskInboundService.checkGraphUpdate(graphId), true));
    }

    @Test
    public void checkSchemaUpdate() {
        Long schemaId = 4L;
        System.out.println(JSON.toJSONString(taskInboundService.checkSchemaUpdate(schemaId), true));
    }
}
