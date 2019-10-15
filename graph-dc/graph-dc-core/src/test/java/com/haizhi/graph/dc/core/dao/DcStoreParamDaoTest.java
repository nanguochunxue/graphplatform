package com.haizhi.graph.dc.core.dao;

import com.haizhi.graph.dc.core.model.po.DcStoreParamPo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengangxiong on 2019/05/05
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcStoreParamDaoTest {

    @Autowired
    private DcStoreParamDao dcStoreParamDao;

    @Test
    public void save(){
        DcStoreParamPo po = new DcStoreParamPo();
        po.setKey("key");
        po.setValue("value");
        po.setStoreId(3L);
        dcStoreParamDao.save(po);
    }
}