package com.haizhi.graph.tag.analytics.task;

import com.haizhi.graph.tag.analytics.task.context.TaskContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengmo on 2018/7/28.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "tag-hdp")
public class SimpleTaskExecutorTest {

    @Test
    public void execute() {
        TaskExecutor taskExecutor = new SimpleTaskExecutor();
        TaskContext ctx = new TaskContext("crm_dev2");
        System.setProperty("HADOOP_USER_NAME", "admin");
        taskExecutor.execute(ctx);
    }
}
