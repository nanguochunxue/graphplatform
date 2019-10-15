package com.haizhi.graph.api.service;

import com.haizhi.graph.dc.core.constant.DcConstants;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.common.service.TaskRedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Create by zhoumingbing on 2019-07-09
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class TaskRedisServiceTest {

    @Autowired
    private TaskRedisService taskRedisService;

    @Test
    public void errorModeTest() {
        Long taskInstanceId = 777L;
        Integer errorMode = null;
        boolean setErrorMode = taskRedisService.setErrorMode(taskInstanceId, errorMode);
        System.out.println(setErrorMode);

        DcInboundDataSuo suo = new DcInboundDataSuo();
        suo.getHeaderOptions().put(DcConstants.KEY_TASK_INSTANCE_ID, taskInstanceId);
        System.out.println(taskRedisService.overErrorMode(suo));
    }
}
