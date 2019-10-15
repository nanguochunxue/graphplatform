package com.haizhi.graph.dc.inbound.dao;

import com.haizhi.graph.dc.core.constant.TaskMetaType;
import com.haizhi.graph.dc.core.model.po.DcTaskMetaPo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengangxiong on 2019/04/23
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcTaskMetaDaoTest {

    @Autowired
    private DcTaskMetaDao taskMetaDao;

    @Test
    public void save(){
        DcTaskMetaPo po = new DcTaskMetaPo();
        po.setDstField("dstfield");
        po.setSrcField("srcfield");
        po.setTaskId(3L);
        po.setType(TaskMetaType.FIELD);
        taskMetaDao.save(po);
    }
}