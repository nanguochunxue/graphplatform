package com.haizhi.graph.dc.core.service;

import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.dao.*;
import com.haizhi.graph.dc.core.model.po.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by chengangxiong on 2019/01/03
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcMetadataServiceTest {

    private static final String TESTER = "tester";

    @Autowired
    private DcGraphDao graphDao;

    @Autowired
    private DcSchemaDao schemaDao;

    @Autowired
    private DcSchemaFieldDao schemaFieldDao;

    @Autowired
    private DcStoreDao storeDao;

    @Autowired
    private DcGraphStoreDao graphStoreDao;

    @Autowired
    private DcVertexEdgeDao vertexEdgeDao;

    @Autowired
    private DcMetadataService metadataService;

    @Autowired
    private DcStoreService storeService;

    @Test
    @Transactional
//    @Rollback
    @Commit
    public void getDomain() {
        dataPrepare();
        Domain domain = metadataService.getDomain("graph_one");
        System.out.println(domain);
        assert !domain.invalid();
    }

    @Test
    public void domain() {
        Domain domain = metadataService.getDomain("graph_one");
        System.out.println(domain);
    }

    public void dataPrepare() {

        DcGraphPo graphOne = new DcGraphPo();
        graphOne.setGraph("graph_one");
        graphOne.setGraphNameCn("中文名");
        graphOne.setCreatedById(TESTER);
        graphOne.setUpdateById(TESTER);
        graphOne.setRemark("remark");

        graphOne = graphDao.save(graphOne);

        DcSchemaPo fromSchema = mkFromSchema(graphOne);
        DcSchemaPo toSchema = mkToSchema(graphOne);
        DcSchemaPo edgeSchema = mkEdgeSchema(graphOne);

        saveGraphStore(graphOne);

        DcVertexEdgePo vertexEdgePo = new DcVertexEdgePo();
        vertexEdgePo.setFromVertex(fromSchema.getSchema());
        vertexEdgePo.setToVertex(toSchema.getSchema());
        vertexEdgePo.setGraph(graphOne.getGraph());
        vertexEdgePo.setEdge(edgeSchema.getSchema());

        vertexEdgeDao.save(vertexEdgePo);

    }

    private void saveGraphStore(DcGraphPo graphOne) {
        DcStorePo storePoHbase = new DcStorePo();
        storePoHbase.setUpdateById(TESTER);
        storePoHbase.setCreatedById(TESTER);
        storePoHbase.setType(StoreType.Hbase);
        storePoHbase.setName("store_hbase_edge");
        storePoHbase.setRemark("hbase store");
        storePoHbase.setUrl("hbase url");

        storePoHbase = storeDao.save(storePoHbase);

        DcStorePo storePoES = new DcStorePo();
        storePoES.setUrl("http://127.0.0.1:9200/");
        storePoES.setRemark("remark remark");
        storePoES.setType(StoreType.ES);
        storePoES.setName("store_es_edge");
        storePoES.setCreatedById(TESTER);
        storePoES.setUpdateById(TESTER);

        storePoES = storeDao.save(storePoES);

        DcStorePo storePoArrango = new DcStorePo();
        storePoArrango.setUpdateById(TESTER);
        storePoArrango.setCreatedById(TESTER);
        storePoArrango.setName("arrango store_edge");
        storePoArrango.setType(StoreType.GDB);
        storePoArrango.setRemark("graph database remark");
        storePoArrango.setUrl("http://127.0.0.1:3333");

        storePoArrango = storeDao.save(storePoArrango);

        DcGraphStorePo graphStorePo = new DcGraphStorePo();
        graphStorePo.setStoreType(StoreType.Hbase);
        graphStorePo.setGraph(graphOne.getGraph());
        graphStorePo.setStoreId(storePoHbase.getId());

        graphStoreDao.save(graphStorePo);

        DcGraphStorePo graphStorePo2 = new DcGraphStorePo();
        graphStorePo2.setStoreType(StoreType.ES);
        graphStorePo2.setGraph(graphOne.getGraph());
        graphStorePo2.setStoreId(storePoES.getId());

        graphStoreDao.save(graphStorePo2);

        DcGraphStorePo graphStorePo3 = new DcGraphStorePo();
        graphStorePo3.setStoreType(StoreType.GDB);
        graphStorePo3.setGraph(graphOne.getGraph());
        graphStorePo3.setStoreId(storePoArrango.getId());

        graphStoreDao.save(graphStorePo3);
    }

    private DcSchemaPo mkFromSchema(DcGraphPo graphOne) {
        DcSchemaPo schemaPo = new DcSchemaPo();
        schemaPo.setUseSearch(true);
        schemaPo.setUseGdb(true);
        schemaPo.setSequence(1);
        schemaPo.setSearchWeight(3);
        schemaPo.setSchema("schema_from");
        schemaPo.setType(SchemaType.VERTEX_MAIN);
        schemaPo.setSchemaNameCn("中文名1");
        schemaPo.setCreatedById(TESTER);
        schemaPo.setUpdateById(TESTER);

        schemaPo = schemaDao.save(schemaPo);

        DcSchemaFieldPo schemaFieldPo = new DcSchemaFieldPo();
        schemaFieldPo.setCreatedById(TESTER);
        schemaFieldPo.setUpdateById(TESTER);
        schemaFieldPo.setSchema(schemaPo.getSchema());
        schemaFieldPo.setType(FieldType.STRING);
        schemaFieldPo.setField("name");
        schemaFieldPo.setFieldNameCn("中文field名称");
        schemaFieldPo.setMain(true);
        schemaFieldPo.setSchema(schemaPo.getSchema());

        schemaFieldDao.save(schemaFieldPo);

        DcSchemaFieldPo schemaFieldPo2 = new DcSchemaFieldPo();
        schemaFieldPo2.setCreatedById(TESTER);
        schemaFieldPo2.setUpdateById(TESTER);
        schemaFieldPo2.setSchema(schemaPo.getSchema());
        schemaFieldPo2.setType(FieldType.LONG);
        schemaFieldPo2.setField("age");
        schemaFieldPo2.setFieldNameCn("中文field名称");
        schemaFieldPo2.setMain(false);
        schemaFieldPo2.setSchema(schemaPo.getSchema());

        schemaFieldDao.save(schemaFieldPo2);

        return schemaPo;
    }

    private DcSchemaPo mkToSchema(DcGraphPo graphOne) {
        DcSchemaPo schemaPo = new DcSchemaPo();
        schemaPo.setUseSearch(true);
        schemaPo.setUseGdb(true);
        schemaPo.setSequence(1);
        schemaPo.setSearchWeight(3);
        schemaPo.setSchema("schema_to");
        schemaPo.setType(SchemaType.VERTEX);
        schemaPo.setSchemaNameCn("中文名2");
        schemaPo.setCreatedById(TESTER);
        schemaPo.setUpdateById(TESTER);

        schemaPo = schemaDao.save(schemaPo);

        DcSchemaFieldPo schemaFieldPo = new DcSchemaFieldPo();
        schemaFieldPo.setCreatedById(TESTER);
        schemaFieldPo.setUpdateById(TESTER);
        schemaFieldPo.setSchema(schemaPo.getSchema());
        schemaFieldPo.setType(FieldType.STRING);
        schemaFieldPo.setField("name2");
        schemaFieldPo.setFieldNameCn("中文field名称");
        schemaFieldPo.setMain(true);
        schemaFieldPo.setSchema(schemaPo.getSchema());

        schemaFieldDao.save(schemaFieldPo);

        DcSchemaFieldPo schemaFieldPo2 = new DcSchemaFieldPo();
        schemaFieldPo2.setCreatedById(TESTER);
        schemaFieldPo2.setUpdateById(TESTER);
        schemaFieldPo2.setSchema(schemaPo.getSchema());
        schemaFieldPo2.setType(FieldType.LONG);
        schemaFieldPo2.setField("age2");
        schemaFieldPo2.setFieldNameCn("中文field名称");
        schemaFieldPo2.setMain(false);
        schemaFieldPo2.setSchema(schemaPo.getSchema());

        schemaFieldDao.save(schemaFieldPo2);

        return schemaPo;
    }

    private DcSchemaPo mkEdgeSchema(DcGraphPo graphOne) {
        DcSchemaPo schemaPo = new DcSchemaPo();
        schemaPo.setUseSearch(true);
        schemaPo.setUseGdb(true);
        schemaPo.setSequence(1);
        schemaPo.setSearchWeight(3);
        schemaPo.setSchema("schema_edge");
        schemaPo.setType(SchemaType.EDGE);
        schemaPo.setSchemaNameCn("中文名edge");
        schemaPo.setCreatedById(TESTER);
        schemaPo.setUpdateById(TESTER);

        schemaPo = schemaDao.save(schemaPo);

        DcSchemaFieldPo schemaFieldPo = new DcSchemaFieldPo();
        schemaFieldPo.setCreatedById(TESTER);
        schemaFieldPo.setUpdateById(TESTER);
        schemaFieldPo.setSchema(schemaPo.getSchema());
        schemaFieldPo.setType(FieldType.STRING);
        schemaFieldPo.setField("name_edge");
        schemaFieldPo.setFieldNameCn("中文field名称");
        schemaFieldPo.setMain(true);
        schemaFieldPo.setSchema(schemaPo.getSchema());

        schemaFieldDao.save(schemaFieldPo);

        DcSchemaFieldPo schemaFieldPo2 = new DcSchemaFieldPo();
        schemaFieldPo2.setCreatedById(TESTER);
        schemaFieldPo2.setUpdateById(TESTER);
        schemaFieldPo2.setSchema(schemaPo.getSchema());
        schemaFieldPo2.setType(FieldType.LONG);
        schemaFieldPo2.setField("age_edge");
        schemaFieldPo2.setFieldNameCn("中文field名称");
        schemaFieldPo2.setMain(false);
        schemaFieldPo2.setSchema(schemaPo.getSchema());

        schemaFieldDao.save(schemaFieldPo2);

        return schemaPo;
    }

    @Test
    public void getFieldMap() {
        List<DcStorePo> dcStorePos = storeService.findByGraph("graph_one");
        Map<String, String> res = dcStorePos.stream().collect(Collectors.toMap(e -> e.getType().name(), e -> e.getUrl()));
        assert res.size() == 3;
    }
}