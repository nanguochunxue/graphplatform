package com.haizhi.graph.dc.core.dao;

import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.dc.core.model.po.DcSchemaPo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengangxiong on 2019/01/03
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcSchemaDaoTest {

    @Autowired
    private DcSchemaDao dcSchemaDao;

    @Test
    public void findBySchema() {

        DcSchemaPo schemaPo = new DcSchemaPo();
        schemaPo.setGraph("graph-one");
        schemaPo.setSchema("schema-name");
        schemaPo.setModifiable(true);
        schemaPo.setSchemaNameCn("schema-cn-name");
        schemaPo.setType(SchemaType.EDGE);
        schemaPo.setRemark("ramark");
        schemaPo.setSearchWeight(2);
        schemaPo.setSequence(3);
        schemaPo.setUseGdb(false);
        schemaPo.setUseSearch(true);
        schemaPo.setCreatedById("tester");
        schemaPo.setUpdateById("tester");

        DcSchemaPo res = dcSchemaDao.save(schemaPo);

        DcSchemaPo findRes = dcSchemaDao.findOne(res.getId());

        Assert.assertEquals(res.getId(), findRes.getId());
    }
}