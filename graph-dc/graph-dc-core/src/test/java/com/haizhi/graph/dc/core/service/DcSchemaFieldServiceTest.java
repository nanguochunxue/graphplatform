package com.haizhi.graph.dc.core.service;

import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.po.DcSchemaFieldPo;
import com.haizhi.graph.dc.core.model.qo.DcSchemaFieldQo;
import com.haizhi.graph.dc.core.model.suo.DcSchemaFieldSuo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by chengangxiong on 2019/01/04
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcSchemaFieldServiceTest {

    @Autowired
    private DcSchemaFieldService schemaFieldService;

    @Test
    public void findByGraph() {
        List<DcSchemaFieldPo> schemaFields = schemaFieldService.findByGraph("graph_one");
        assert schemaFields.size() == 6;
    }

    @Test
    public void findPage(){
        DcSchemaFieldQo qo = new DcSchemaFieldQo();
        qo.setSearch("field__");
        qo.setType(FieldType.LONG);
        PageResponse res = schemaFieldService.findPage(qo);
        assert res.isSuccess();
    }

    @Test
    public void save(){
        DcSchemaFieldSuo suo = new DcSchemaFieldSuo();
        suo.setField("field_");
        suo.setFieldNameCn("中文_");
        suo.setSchema("schema_");
        suo.setType(FieldType.STRING);
        suo.setSearchWeight(2);
        Response res = schemaFieldService.saveOrUpdate(suo);
        assert res.isSuccess();
    }

    @Test
    public void update(){
        DcSchemaFieldSuo suo = new DcSchemaFieldSuo();
        suo.setId(9L);
        suo.setField("field__");
        suo.setFieldNameCn("中文__");
        suo.setSchema("schema__");
        suo.setType(FieldType.LONG);
        suo.setSearchWeight(4);
        Response res = schemaFieldService.saveOrUpdate(suo);
        assert res.isSuccess();
    }

    @Test
    public void delete(){
        Response res = schemaFieldService.delete(9L);
        assert res.isSuccess();
    }
}