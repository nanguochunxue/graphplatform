package com.haizhi.graph.server.arango.admin;

import com.alibaba.fastjson.JSON;
import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentImportEntity;
import com.arangodb.model.DocumentImportOptions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.gdb.admin.GdbAdminDao;
import com.haizhi.graph.server.api.gdb.admin.model.GdbSuo;
import com.haizhi.graph.server.api.gdb.search.GdbQuery;
import com.haizhi.graph.server.api.gdb.search.GdbQueryResult;
import com.haizhi.graph.server.api.gdb.search.GdbSearchDao;
import com.haizhi.graph.server.arango.client.ArangoClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * Created by tanghaiyang on 2019/5/10.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class ArangoAdminDAOTest {

    private static final GLog LOG = LogFactory.getLogger(ArangoAdminDAOTest.class);
    private static final String graph = "demo_graph1";
    private static final String schema = "demo_vertex";

    @Autowired
    private GdbAdminDao gdbAdminDao;

    @Autowired
    private GdbSearchDao gdbSearchDao;

    @Autowired
    private ArangoClient arangoClient;

    private StoreURL storeURL;

    @Before
    public void init(){
        storeURL = new StoreURL();
        //storeURL.setUrl("192.168.1.176:8529");
        /*storeURL.setUrl("192.168.1.176:8529,192.168.1.177:8529,192.168.1.178:8529");
        storeURL.setUser("dmp_manager");
        storeURL.setPassword("dmp_manager@2019");*/

        /*storeURL.setUrl("192.168.1.36:8529");
        storeURL.setUser("");
        storeURL.setPassword("");*/

        storeURL.setUrl("192.168.1.128:18530");
        storeURL.setUser("root");
        storeURL.setPassword("123456");
    }

    @Test
    public void testExample(){
        // create
        if (!gdbAdminDao.existsDatabase(storeURL, graph)){
            gdbAdminDao.createDatabase(storeURL, graph);
        }
        GdbSuo gdbSuo = new GdbSuo();
        gdbSuo.setGraph(graph);
        gdbSuo.setSchema(schema);
        gdbSuo.setType(SchemaType.VERTEX);
        if (!gdbAdminDao.existsTable(storeURL, graph, schema)){
            gdbAdminDao.createTable(storeURL, gdbSuo);
        }

        // bulk
        Map<String, Object> map = new HashMap<>();
        //map.put("object_key", "1000");
        map.put("object_key", "A6D0359EFBB398C09A64F106DBC59E46");
        map.put("_key", "A6D0359EFBB398C09A64F106DBC59E46");
        map.put("_rev", "_YqpwWA---_");
        map.put("demo_string_field", "test field");
        map.put("demo_long_field", "222222");
        map.put("demo_double_field", "884.33");
        map.put("demo_date_field", "2019-05-21");
        gdbSuo.setRows(Lists.newArrayList(map));
        gdbSuo.setOperation(GOperation.CREATE_OR_UPDATE);
        gdbAdminDao.bulkPersist(storeURL, gdbSuo);

        // query
        GdbQuery gdbQuery = new GdbQuery(graph);
        Map<String, Set<String>> schemaMap = new HashMap<>();
        schemaMap.put(schema, Sets.newHashSet("A6D0359EFBB398C09A64F106DBC59E46"));
        gdbQuery.setSchemas(schemaMap);
        GdbQueryResult gdbResult = gdbSearchDao.findByIds(storeURL, gdbQuery);
        JSONUtils.println(gdbResult);
    }

    @Test
    public void findByIds(){
        // query
        GdbQuery gdbQuery = new GdbQuery(graph);
        Map<String, Set<String>> schemaMap = new HashMap<>();
        schemaMap.put(schema, Sets.newHashSet("1000"));
        gdbQuery.setSchemas(schemaMap);
        GdbQueryResult gdbResult = gdbSearchDao.findByIds(storeURL, gdbQuery);
        JSONUtils.println(gdbResult);
    }

    @Test
    public void deleteTable(){
        GdbSuo gdbSuo = new GdbSuo();
        gdbSuo.setGraph(graph);
        gdbSuo.setSchema(schema);
        gdbSuo.setType(SchemaType.VERTEX);
        gdbAdminDao.deleteTable(storeURL, gdbSuo);
    }

    @Test
    public void bulkPersist(){
        GdbSuo gdbSuo = new GdbSuo();
        gdbSuo.setGraph("test");
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "sdfsdf");
        rows.add(map);
        gdbSuo.setRows(rows);

        Map<String, FieldType> mapField = new HashMap<>();
        mapField.put("name", FieldType.STRING);
        gdbSuo.setFields(mapField);
        gdbSuo.setOperation(GOperation.UPDATE);
        gdbAdminDao.bulkPersist(storeURL, gdbSuo);
    }

    /*
    * no key:
    * OnDuplicate.replace and OnDuplicate.update will add record
    *
    * has key:
    * OnDuplicate.replace will replace old data
    * OnDuplicate.update will add map or update
    *
    * */
    @Test
    public void importDocuments(){
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("ssss", "bbbb");
        map.put("_key", "fff");
        rows.add(map);
        ArangoDatabase adb = arangoClient.getClient(storeURL).db("test");
        ArangoCollection ac = adb.collection("testCollection");

        DocumentImportOptions documentImportOptions = new DocumentImportOptions();
//        documentImportOptions.onDuplicate(DocumentImportOptions.OnDuplicate.replace);
        documentImportOptions.onDuplicate(DocumentImportOptions.OnDuplicate.update);
        DocumentImportEntity retObj = ac.importDocuments(rows, documentImportOptions);
        LOG.info("retObj:{0}", JSON.toJSONString(retObj, true));
    }


}
