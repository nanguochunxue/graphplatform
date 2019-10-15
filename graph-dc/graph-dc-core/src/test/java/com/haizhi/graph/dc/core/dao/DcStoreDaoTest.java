package com.haizhi.graph.dc.core.dao;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.dc.core.model.po.DcStorePo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by chengangxiong on 2019/01/02
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcStoreDaoTest {

    private static final Logger log = LoggerFactory.getLogger(DcStoreDaoTest.class);

    @Autowired
    private DcStoreDao dcStoreDao;

    @Test
    public void findOne() {
        DcStorePo dcStorePo = new DcStorePo();
        dcStorePo.setUser("user");
        dcStorePo.setPassword("password");
        dcStorePo.setName("dc-name");
        dcStorePo.setRemark("remark");
        dcStorePo.setType(StoreType.Hbase);
        dcStorePo.setUrl("http://www.baidu.com");
        DcStorePo res = dcStoreDao.save(dcStorePo);
        assertEquals(res.getId(), dcStorePo.getId());
    }
}