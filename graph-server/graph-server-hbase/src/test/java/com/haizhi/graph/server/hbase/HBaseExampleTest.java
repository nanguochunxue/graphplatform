package com.haizhi.graph.server.hbase;

import com.google.common.collect.Sets;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.key.KeyFactory;
import com.haizhi.graph.common.key.KeyGetter;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.admin.HBaseAdminDao;
import com.haizhi.graph.server.api.hbase.admin.bean.HBaseRows;
import com.haizhi.graph.server.api.hbase.query.HBaseQueryDao;
import com.haizhi.graph.server.api.hbase.query.bean.HBaseQuery;
import com.haizhi.graph.server.api.hbase.query.bean.HBaseQueryResult;
import com.haizhi.graph.server.api.hbase.query.bean.HBaseRangeQuery;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chengmo on 2019/7/4.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class HBaseExampleTest {
    private static final String DATABASE = "demo_graph1";
    private static final String TABLE = "demo_vertex";
    private static KeyGetter keyGetter = KeyFactory.createKeyGetter();
    private static StoreURL storeURL;

    @Autowired
    HBaseAdminDao hBaseAdminDao;
    @Autowired
    HBaseQueryDao hBaseQueryDao;

    @BeforeClass
    public static void init(){
        // org.apache.hadoop.hbase.security.AccessDeniedException:
        // Insufficient permissions for user 'chengmo' (action=admin)
        //storeURL = StoreURLFactory.createHBase_CDH();
        //storeURL = StoreURLFactory.createHBase_FIC80();
        storeURL = StoreURLFactory.createHBase_KSYUN();
        //System.setProperty("HADOOP_USER_NAME", storeURL.getUserPrincipal());
    }

    @Test
    public void simple() {
        createDatabaseIfNotExists();
        createTableIfNotExists();
        listTableNames();
        bulkUpsert();
        rangeScanQuery();
        searchByKeys();
        //deleteByRowKeys();
    }

    @Test
    public void deleteTable() {
        if (hBaseAdminDao.existsTable(storeURL, DATABASE, TABLE)) {
            hBaseAdminDao.deleteTable(storeURL, DATABASE, TABLE);
        }
        System.out.println("================================================");
    }

    @Test
    public void createDatabaseIfNotExists() {
        if (!hBaseAdminDao.existsDatabase(storeURL, DATABASE)) {
            hBaseAdminDao.createDatabase(storeURL, DATABASE);
        }
        System.out.println("================================================");
    }

    @Test
    public void createTableIfNotExists() {
        if (!hBaseAdminDao.existsTable(storeURL, DATABASE, TABLE)) {
            hBaseAdminDao.createTable(storeURL, DATABASE, TABLE, true);
        }
        System.out.println("================================================");
    }

    @Test
    public void listTableNames() {
        List<String> tables = hBaseAdminDao.listTableNames(storeURL, "demo.*");
        JSONUtils.println(tables);
        System.out.println("================================================");
    }

    @Test
    public void bulkUpsert() {
        HBaseRows rows = new HBaseRows();
        Map<String, Object> map = new HashMap<>();
        map.put("object_key", "1000");
        map.put("demo_string_field", "华为技术有限公司");
        map.put("demo_string_field2", "华为技术有限公司");
        map.put("demo_string_field3", "广东");
        map.put("demo_long_field", 100);
        map.put("demo_double_field", 200.99);
        map.put("demo_date_field", "2019-05-21 18:45:56");
        rows.addRow(map);
        map = new HashMap<>();
        map.put("object_key", "1001");
        map.put("demo_string_field", "百度网络科技有限公司");
        map.put("demo_string_field2", "百度网络科技有限公司");
        map.put("demo_string_field3", "北京");
        map.put("demo_long_field", 101);
        map.put("demo_double_field", 201.99);
        map.put("demo_date_field", "2019-05-22");
        rows.addRow(map);
        CudResponse resp = hBaseAdminDao.bulkUpsert(storeURL, DATABASE, TABLE, rows);
        JSONUtils.println(resp);
        System.out.println("================================================");
    }

    @Test
    public void searchByKeys() {
        HBaseQuery query = new HBaseQuery(DATABASE);
        query.addRowKeys(TABLE, keyGetter.getRowKey("1000"), keyGetter.getRowKey("1001"));
        HBaseQueryResult result = hBaseQueryDao.searchByKeys(storeURL, query);
        JSONUtils.println(result);
        System.out.println("================================================");
    }

    @Test
    public void rangeScanQuery() {
        HBaseRangeQuery query = new HBaseRangeQuery(DATABASE, TABLE);
        query.setRange("000#", "999#");
        List<Map<String, String>> result = hBaseQueryDao.rangeScanQuery(storeURL, query);
        JSONUtils.println(result);
        System.out.println("================================================");
    }

    @Test
    public void deleteByRowKeys() {
        Set<String> rowKeys = Sets.newHashSet(keyGetter.getRowKey("1000"),
                keyGetter.getRowKey("1001"));
        CudResponse resp = hBaseAdminDao.deleteByRowKeys(storeURL, DATABASE, TABLE, rowKeys);
        JSONUtils.println(resp);
        System.out.println("================================================");
    }
}
