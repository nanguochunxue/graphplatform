package com.haizhi.graph.tag.analytics.task;

import com.haizhi.graph.tag.analytics.task.context.TaskContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengmo on 2018/4/11.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi-tdh")
public class DagTaskExecutorTest {

    @Test
    public void execute() {
        TaskExecutor taskExecutor = new DagTaskExecutor();
        TaskContext ctx = new TaskContext("crm_dev2");
        ctx.addTagIds(401001L);
        //ctx.addTagIds(401001L, 402001L, 402002L);
        taskExecutor.execute(ctx);
    }
}
