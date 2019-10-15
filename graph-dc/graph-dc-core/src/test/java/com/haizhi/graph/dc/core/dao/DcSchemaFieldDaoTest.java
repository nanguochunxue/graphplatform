package com.haizhi.graph.dc.core.dao;

import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.dc.core.model.po.DcSchemaFieldPo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by chengangxiong on 2019/01/02
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcSchemaFieldDaoTest {

    @Autowired
    public DcSchemaFieldDao dcSchemaFieldDao;

    @Test
    public void save() {
        DcSchemaFieldPo dcSchemaFieldPo = new DcSchemaFieldPo();
        dcSchemaFieldPo.setGraph("graph-one");
        dcSchemaFieldPo.setField("name");
        dcSchemaFieldPo.setFieldNameCn("中文");
        dcSchemaFieldPo.setMain(false);
        dcSchemaFieldPo.setSchema("schema");
        dcSchemaFieldPo.setType(FieldType.STRING);
        dcSchemaFieldPo.setModifiable(true);
        dcSchemaFieldPo.setSequence(3);

        dcSchemaFieldPo.setUpdateById("tester");
        dcSchemaFieldPo.setCreatedById("tester");

        DcSchemaFieldPo res = dcSchemaFieldDao.save(dcSchemaFieldPo);

        assertEquals(res.getField(), dcSchemaFieldPo.getField());
        assertNotNull(res.getId());

    }

    @Test
    public void test2(){
        List<DcSchemaFieldPo> res = dcSchemaFieldDao.findByGraphAndSchemaAndIsMain("graph_one", "schema_one", true);
        System.out.println(res);
    }
}