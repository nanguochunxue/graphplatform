package com.haizhi.graph.dc.core.service;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.po.DcSchemaPo;
import com.haizhi.graph.dc.core.model.qo.DcSchemaQo;
import com.haizhi.graph.dc.core.model.suo.DcSchemaSuo;
import com.haizhi.graph.dc.core.model.vo.DcGraphStoreVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * Created by chengangxiong on 2019/01/03
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcSchemaServiceTest {

    @Autowired
    private DcSchemaService dcSchemaService;

    @Test
    public void findByGraph() {
        List<DcSchemaPo> schemas = dcSchemaService.findByGraph("graph_one");
        assert schemas.size() != 0;
    }

    @Test
    public void findPage() {
        DcSchemaQo qo = new DcSchemaQo();
        qo.setSearch("schema");
        qo.setType(SchemaType.EDGE);
        PageResponse res = dcSchemaService.findPage(qo);
        assert res.isSuccess();
    }

    @Test
    public void update() {
        DcSchemaSuo suo = new DcSchemaSuo();
        suo.setId(3L);
        suo.setRemark("remark_");
        suo.setGraph("aaaaa");
        suo.setSchema("schema_");
        suo.setSequence(99);
        suo.setType(SchemaType.VERTEX);
        suo.setSearchWeight(99);
        suo.setSchemaNameCn("中文_");
        Response res = dcSchemaService.saveOrUpdate(suo);
        assert res.isSuccess();
    }

    @Test
    public void save() {
        DcSchemaSuo suo = new DcSchemaSuo();
        suo.setGraph("aaaaa");
        suo.setRemark("remark_1");
        suo.setSchema("schema_1");
        suo.setSequence(12);
        suo.setType(SchemaType.VERTEX);
        suo.setSearchWeight(12);
        suo.setSchemaNameCn("中文_1");
        Response res = dcSchemaService.saveOrUpdate(suo);
        assert res.isSuccess();
    }

    @Test
    public void delete() {
        Response res = dcSchemaService.delete(4L);
        assert res.isSuccess();
    }

    @Test
    public void countByGraph() {
        String graph = "sz_haizhi_db";
        DcGraphStoreVo dcGraphStoreVo = dcSchemaService.countByGraph(graph);
        System.out.println(JSON.toJSONString(dcGraphStoreVo, true));
    }
}