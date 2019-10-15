package com.haizhi.graph.server.tiger.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.gdb.admin.GdbAdminDao;
import com.haizhi.graph.server.api.gdb.admin.model.GdbSuo;
import com.haizhi.graph.server.tiger.repository.TigerRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * Created by chengmo on 2019/3/5.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
@EnableJpaRepositories({"com.haizhi.graph"})
@EntityScan({"com.haizhi.graph"})
@ComponentScan({"com.haizhi.graph"})
public class GdbAdminDaoTest {

    private static final GLog LOG = LogFactory.getLogger(GdbAdminDaoTest.class);

    private static String database = "work_graph";
    private static String url = "http://192.168.1.101:9000/graph";
    private static String gsqlUrl = "http://192.168.1.101:14240/gsqlserver/gsql/file#tigergraph:tigergraph";

    @Autowired
    GdbAdminDao gdbAdminDao;

    @Autowired
    private TigerRepo tigerRepo;

    private StoreURL storeURL;

    @Test
    public void existsDatabase(){
        boolean ret = gdbAdminDao.existsDatabase(storeURL, database);
        LOG.info("ret: {0}", ret);
    }

    @Test
    public void existsTable(){
        String table = "company";
        boolean ret = gdbAdminDao.existsTable(storeURL, database, table);
        LOG.info("ret: {0}", ret);

        String table_not_exist = "company_not_exist";
        boolean ret_not_exist = gdbAdminDao.existsTable(storeURL, database, table_not_exist);
        LOG.info("ret_not_exist: {0}", ret_not_exist);
    }

    // tiger-developer version do not support
    @Test
    public void createDatabase(){
        gdbAdminDao.createDatabase(storeURL, database);
    }


    @Test
    public void createTable(){
        GdbSuo suo = new GdbSuo();
        suo.setGraph(database);
        suo.setSchema("company");
        suo.setType(SchemaType.VERTEX);
        Map<String,FieldType> fields = new LinkedHashMap<>();
        fields.put("id", FieldType.LONG);
        fields.put("age", FieldType.LONG);
        fields.put("name", FieldType.STRING);
        suo.setFields(fields);
        gdbAdminDao.createTable(storeURL, suo);
    }

    // tiger-developer version do not support
    @Test
    public void deleteTable(){
        GdbSuo suo = new GdbSuo();
        suo.setGraph(database);
        gdbAdminDao.deleteTable(storeURL, suo);
    }

    @Test
    public void bulkPersist(){
        GdbSuo suoVertices = buildVerticesTest();
        LOG.info("suoVertices:\n{0}",suoVertices);
        CudResponse responseVertices = gdbAdminDao.bulkPersist(storeURL, suoVertices);
        LOG.info("responseVertices: \n{0}", responseVertices);

        GdbSuo suoEdges = buildEdgesTest();
        LOG.info("suoEdges:\n{0}",suoEdges);
        CudResponse responseEdges = gdbAdminDao.bulkPersist(storeURL, suoEdges);
        LOG.info("responseEdges: \n{0}", responseEdges);
    }

    @Test
    public void deleteData(){
        GdbSuo suoVertices = buildVerticesTest();
        CudResponse responseVertices = gdbAdminDao.delete(storeURL, suoVertices);
        LOG.info("responseVertices: \n{0}", responseVertices);

        GdbSuo suoEdges = buildEdgesTest();
        CudResponse responseEdges = gdbAdminDao.delete(storeURL, suoEdges);
        LOG.info("responseEdges: \n{0}", responseEdges);
    }

    @Test
    public void executeGsql() throws Exception{
        String gsql = "USE GRAPH work_graph\nls";
        JSONObject result = tigerRepo.execute(gsqlUrl, gsql);
        LOG.info(JSON.toJSONString(result, SerializerFeature.PrettyFormat));

//        gsql = "USE GRAPH work_graph\nSELECT * FROM persons LIMIT 3";
//        result = tigerRepo.execute(gsqlUrl, gsql);
//        LOG.info(result);
    }

    public static GdbSuo buildVerticesTest(){
        GdbSuo suo = new GdbSuo();
        suo.setGraph("work_graph");
        suo.setSchema("company");
        suo.setType(SchemaType.VERTEX);
        suo.setOperation(GOperation.UPDATE);

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put(Keys.OBJECT_KEY, "c2");
        map1.put("city", "dongguan");
        map1.put("quadrant", 2);
        map1.put("company_name", "com2");
        map1.put("sex", "male");
        map1.put("id", "c2");
        map1.put("age", 2);
        rows.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put(Keys.OBJECT_KEY, "c1");
        map2.put("city", "sanya");
        map2.put("quadrant", 1);
        map2.put("company_name", "com1");
        map2.put("sex", "female");
        map2.put("id", "c1");
        map2.put("age", 1);
        rows.add(map2);

        suo.setRows(rows);

        return suo;
    }

    public static GdbSuo buildEdgesTest(){
        GdbSuo suo = new GdbSuo();
        suo.setGraph("work_graph");
        suo.setSchema("all_to_skill");
        suo.setType(SchemaType.EDGE);
        suo.setOperation(GOperation.UPDATE);

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put(Keys._FROM, "Company/p1");
        map1.put(Keys._TO, "skill/s1");
        map1.put("quadrant", 2);
        rows.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put(Keys._FROM, "persons/p8");
        map2.put(Keys._TO, "skill/s1");
        map2.put("quadrant", 1);
        rows.add(map2);
        suo.setRows(rows);
        return suo;
    }

}
