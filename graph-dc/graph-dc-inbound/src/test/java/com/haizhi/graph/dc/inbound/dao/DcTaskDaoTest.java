package com.haizhi.graph.dc.inbound.dao;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.dc.core.constant.ExecutionType;
import com.haizhi.graph.dc.core.constant.TaskState;
import com.haizhi.graph.dc.core.constant.TaskType;
import com.haizhi.graph.dc.core.model.po.DcTaskPo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengangxiong on 2019/01/29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcTaskDaoTest {

    @Autowired
    private DcTaskDao dcTaskDao;

    @Test
    public void insert(){

        DcTaskPo dcTaskPo = new DcTaskPo();
        dcTaskPo.setGraph("graph-test");
        dcTaskPo.setSchema("schema-test");
        dcTaskPo.setSource("source");
        dcTaskPo.setStoreId(3L);
        dcTaskPo.setTaskType(TaskType.HDFS);
        dcTaskPo.setTaskState(TaskState.NORMAL);
        dcTaskPo.setTaskName("taskName22");
        dcTaskPo.setLastInstanceId(90L);
        dcTaskPo.setRemark("remark");
        dcTaskPo.setCron("ccron");
        dcTaskPo.setExecutionType(ExecutionType.ONCE);

        dcTaskPo = dcTaskDao.save(dcTaskPo);
        assert dcTaskPo.getId() != null;
    }

    private static final GLog log = LogFactory.getLogger(DcTaskDaoTest.class);

    @Test
    public void find(){
        DcTaskPo dcTaskPo2 = dcTaskDao.findOne(2L);
        assert dcTaskPo2.getTaskState() == TaskState.NORMAL;
    }
}