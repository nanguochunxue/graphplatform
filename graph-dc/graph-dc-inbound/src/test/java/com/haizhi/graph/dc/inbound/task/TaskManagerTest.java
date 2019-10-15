package com.haizhi.graph.dc.inbound.task;

import com.haizhi.graph.dc.core.constant.TaskState;
import com.haizhi.graph.dc.core.model.po.DcTaskPo;
import com.haizhi.graph.dc.inbound.service.DcTaskService;
import com.haizhi.graph.dc.inbound.task.conf.DcTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
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
public class TaskManagerTest {

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private DcTaskService dcTaskService;

    @Test
    public void submitForGp() throws SchedulerException, InterruptedException {
        DcTaskPo po = dcTaskService.findOne(3L);
        DcTask dcTask = new DcTask(po);
        po.setTaskState(TaskState.RUNNING);
        po.setLastInstanceId(0L);
        taskManager.submit(dcTask);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }
}