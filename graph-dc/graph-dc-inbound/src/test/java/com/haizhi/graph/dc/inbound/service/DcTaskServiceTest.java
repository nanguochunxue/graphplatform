package com.haizhi.graph.dc.inbound.service;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.model.PageQo;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.dc.core.constant.ExecutionType;
import com.haizhi.graph.dc.core.constant.TaskInstanceState;
import com.haizhi.graph.dc.core.constant.TaskType;
import com.haizhi.graph.dc.core.model.po.DcTaskPo;
import com.haizhi.graph.dc.core.model.qo.ApiInboundQo;
import com.haizhi.graph.dc.core.model.qo.BatchInboundQo;
import com.haizhi.graph.dc.core.model.qo.DcTaskQo;
import com.haizhi.graph.dc.core.model.qo.FlumeInboundQo;
import com.haizhi.graph.dc.core.model.vo.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by chengangxiong on 2019/02/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcTaskServiceTest {

    @Autowired
    private DcTaskService dcTaskService;

    @Test
    public void findOne() {
        DcTaskVo result = dcTaskService.taskDetail(45L);
        assert result.getId() == 45L;
    }

    @Test
    public void findPage() {
        DcTaskQo dcTaskQo = new DcTaskQo();
        dcTaskQo.setExecutionType(ExecutionType.ONCE);
        dcTaskQo.setTaskInstanceState(TaskInstanceState.READY);
        dcTaskQo.setTaskName("demo_vertex_gp");
        dcTaskQo.setTaskType(TaskType.GREEPLUM);
        PageQo pageQo = new PageQo(1, 2);
        dcTaskQo.setPage(pageQo);

        PageResponse<DcTaskPageVo> res = dcTaskService.findPage(dcTaskQo);

        System.out.println(res.getPayload());
    }

    @Test
    public void apiTaskPage() {
        ApiInboundQo qo = new ApiInboundQo();
        qo.setStoreId(99L);
        qo.getPage().setPageSize(8);
        qo.getPage().setPageNo(1);
        PageResponse<ApiInboundVo> res = dcTaskService.findTaskPage(qo);
        System.out.println(res);
    }

    @Test
    public void flumeTaskPage() {
        FlumeInboundQo qo = new FlumeInboundQo();
        qo.setStoreId(99L);
        qo.getPage().setPageSize(8);
        qo.getPage().setPageNo(1);
        PageResponse<FlumeInboundVo> res = dcTaskService.findTaskPage(qo);
        System.out.println(res);
    }

    @Test
    public void batchTaskPage() {
        BatchInboundQo qo = new BatchInboundQo();
        qo.setStoreId(99L);
        qo.getPage().setPageSize(8);
        qo.getPage().setPageNo(1);
        PageResponse<BatchInboundVo> res = dcTaskService.findTaskPage(qo);
        System.out.println(res);
    }

    @Test
    public void findByStoreId() {
        Long storeId = 0L;
        List<DcTaskPo> dcTaskPos = dcTaskService.findByStoreId(storeId);
        for (DcTaskPo dcTaskPo : dcTaskPos) {
            System.out.println(JSON.toJSONString(dcTaskPo));
        }
    }
}