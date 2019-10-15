package com.haizhi.graph.dc.core.dao;

import com.haizhi.graph.dc.core.model.po.DcVertexEdgePo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Created by chengangxiong on 2019/01/03
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcVertexEdgeDaoTest {

    @Autowired
    private DcVertexEdgeDao dcVertexEdgeDao;

    @Test
    @Transactional
    @Rollback
    public void save() {
        DcVertexEdgePo dcVertexEdgePo = new DcVertexEdgePo();
        dcVertexEdgePo.setEdge("edge");
        dcVertexEdgePo.setFromVertex("aaaaa");
        dcVertexEdgePo.setGraph("graph-name");
        dcVertexEdgePo.setToVertex("to-schema");

        DcVertexEdgePo po = dcVertexEdgeDao.save(dcVertexEdgePo);

        assertNotNull(po.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void find() {

        DcVertexEdgePo dcVertexEdgePo = new DcVertexEdgePo();
        dcVertexEdgePo.setEdge("edge");
        dcVertexEdgePo.setFromVertex("aaaaa");
        dcVertexEdgePo.setGraph("graph-name");
        dcVertexEdgePo.setToVertex("to-schema");

        DcVertexEdgePo po = dcVertexEdgeDao.save(dcVertexEdgePo);

        DcVertexEdgePo findRes = dcVertexEdgeDao.findOne(Example.of(dcVertexEdgePo));
        assertEquals(po.getId(), findRes.getId());
    }
}