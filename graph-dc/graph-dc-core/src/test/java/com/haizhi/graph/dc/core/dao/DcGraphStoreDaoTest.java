package com.haizhi.graph.dc.core.dao;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.dc.core.model.po.DcGraphStorePo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by chengangxiong on 2019/01/03
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcGraphStoreDaoTest {

    @Autowired
    public DcGraphStoreDao dcGraphStoreDao;

    @Test
    @Transactional
    @Rollback
    public void save() {
        DcGraphStorePo dcGraphStorePo = new DcGraphStorePo();

        dcGraphStorePo.setGraph("graph");
        dcGraphStorePo.setStoreId(2L);
        dcGraphStorePo.setStoreType(StoreType.ES);

        DcGraphStorePo res = dcGraphStoreDao.save(dcGraphStorePo);
        assertEquals(res.getGraph(), dcGraphStorePo.getGraph());
        assertNotNull(res.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void findByGraph() {

        DcGraphStorePo dcGraphStorePo = new DcGraphStorePo();

        dcGraphStorePo.setGraph("graph");
        dcGraphStorePo.setStoreId(2L);
        dcGraphStorePo.setStoreType(StoreType.ES);

        DcGraphStorePo res = dcGraphStoreDao.save(dcGraphStorePo);

        DcGraphStorePo queryRes = dcGraphStoreDao.findOne(Example.of(dcGraphStorePo));

        assertEquals(res.getId(), queryRes.getId());
    }

    @Test
    public void deleteByGraph(){
        dcGraphStoreDao.deleteByGraph("test_graph");
    }

}