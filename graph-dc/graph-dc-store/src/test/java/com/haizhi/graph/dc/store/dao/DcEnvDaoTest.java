package com.haizhi.graph.dc.store.dao;

import com.haizhi.graph.dc.core.dao.DcEnvDao;
import com.haizhi.graph.dc.core.model.po.DcEnvPo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengangxiong on 2019/03/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcEnvDaoTest {

    @Autowired
    private DcEnvDao dcEnvDao;

    @Test
    public void test(){
        DcEnvPo po = new DcEnvPo();
        po.setName("test");
        po.setUser("user");
        po.setRemark("remark");
        dcEnvDao.save(po);
    }
}