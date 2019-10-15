package com.haizhi.graph.dc.inbound.dao;

import com.haizhi.graph.dc.core.constant.OperateType;
import com.haizhi.graph.dc.core.constant.TaskInstanceState;
import com.haizhi.graph.dc.core.model.po.DcTaskInstancePo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chengangxiong on 2019/01/29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcTaskInstanceDaoTest {

    @Autowired
    private DcTaskInstanceDao dcTaskInstanceDao;

    @Test
    public void save(){
        DcTaskInstancePo po = new DcTaskInstancePo();
        po.setTaskId(22L);
        po.setEsAffectedRows(3);
        po.setGdbAffectedRows(20);
        po.setHbaseAffectedRows(19);
        po.setOperateType(OperateType.INIT);
        po.setState(TaskInstanceState.READY);
        po.setTotalRows(900);
        po.setTotalSize(30000000);
        assert dcTaskInstanceDao.save(po).getId() != null;
    }

    @Test
    public void find(){
        DcTaskInstancePo dcTaskInstance = dcTaskInstanceDao.findOne(3L);
        assert dcTaskInstance.getState() == TaskInstanceState.READY;
    }

    @Test
    public void test() throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2019-03-09");
        DcTaskInstancePo po = dcTaskInstanceDao.findByOperateDt(date);
        System.out.println(po);
    }
}