package com.haizhi.graph.dc.core.dao;

import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.dc.core.model.po.DcGraphPo;
import lombok.NonNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by chengangxiong on 2018/12/25
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcGraphDaoTest {

    @Autowired
    private DcGraphDao dcGraphDao;

    @Test
    public void findByGraph() {
        DcGraphPo dcGraphPo = new DcGraphPo();
        dcGraphPo.setGraph("test-graph-name");
        dcGraphPo.setGraphNameCn("图名称-中文");
        dcGraphPo.setRemark("remark");
        DcGraphPo res = dcGraphDao.save(dcGraphPo);
        DcGraphPo graph = dcGraphDao.findByGraph(dcGraphPo.getGraph());
        assertEquals(graph.getId(), res.getId());
    }

    @Test
    public void findByGraphException() {
        @NonNull
        DcGraphPo res = dcGraphDao.findByGraph("aa-a-a-aaaa-a");
        assertNull(res);
    }

    @Test
    public void save() {
        DcGraphPo dcGraphPo = new DcGraphPo();
        dcGraphPo.setId(24L);
        dcGraphPo.setGraph("test-graph-name____");
        dcGraphPo.setGraphNameCn("图名称-中文__");
        dcGraphPo.setRemark("remark__");
        DcGraphPo res = dcGraphDao.save(dcGraphPo);
        assert res.getId() != null;
    }

    @Test
    public void example(){

        DcGraphPo dcGraphPo = new DcGraphPo();
        dcGraphPo.setGraph("graph_one");

        List<DcGraphPo> res = dcGraphDao.findAll(Example.of(dcGraphPo));
        assert res.size() == 1;
    }

    @Test
    public void findOne(){
        long start = System.currentTimeMillis();
        DcGraphPo data = dcGraphDao.findOne(1L);
        DcGraphPo data2 = dcGraphDao.findOne(1L);
        System.out.println("time cost : " + (System.currentTimeMillis() - start));
    }

    @Test
    public void saveOne(){
        DcGraphPo po = new DcGraphPo();
        po.setGraph("hhhhhhhhaaaaaaa");
        po.setGraphNameCn("chinese name");
        po.setRemark("remark");
        po = dcGraphDao.save(po);
        DcGraphPo result = dcGraphDao.findByGraph(po.getGraph());
        JSONUtils.println(result);
    }
}