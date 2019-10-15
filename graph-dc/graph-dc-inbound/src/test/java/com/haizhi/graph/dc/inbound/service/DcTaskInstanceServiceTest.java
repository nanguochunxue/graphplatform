package com.haizhi.graph.dc.inbound.service;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.dc.core.model.po.DcTaskInstancePo;
import com.haizhi.graph.dc.core.model.qo.DcTaskInstanceQo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengangxiong on 2019/02/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcTaskInstanceServiceTest {

    @Autowired
    private DcTaskInstanceService dcTaskInstanceService;

    @Test
    public void findByTaskId(){

        DcTaskInstancePo res = dcTaskInstanceService.findInstanceDetailByTaskId(1L);
        System.out.println(JSON.toJSONString(res, true));
    }

    @Test
    public void findPage(){
        DcTaskInstanceQo qo = new DcTaskInstanceQo();
        qo.setTaskId(1L);

        PageResponse res = dcTaskInstanceService.findPage(qo);
        System.out.println(res);
    }
}