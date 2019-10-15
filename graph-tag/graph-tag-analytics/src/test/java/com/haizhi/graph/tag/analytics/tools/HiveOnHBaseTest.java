package com.haizhi.graph.tag.analytics.tools;

import com.haizhi.graph.common.context.Resource;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.engine.flow.tools.hive.HiveHelper;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.admin.HBaseAdminDao;
import com.haizhi.graph.server.api.hbase.admin.bean.HBaseRows;
import com.haizhi.graph.server.api.hbase.query.HBaseQueryDao;
import com.haizhi.graph.server.api.hbase.query.bean.HBaseQuery;
import com.haizhi.graph.server.api.hbase.query.bean.HBaseQueryResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/7/30.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi-tdh")
public class HiveOnHBaseTest {

    static final String GRAPH = "test_graph";
    static final String TABLE = "test_table";
    static final String TABLE_NAME = GRAPH + ":" + TABLE;
    static final String ACTIVE_PROFILE = "lab-bdp-tdh.properties";

    @Autowired
    HBaseAdminDao hBaseAdminDao;
    @Autowired
    HBaseQueryDao hBaseQueryDao;

    private StoreURL storeURL;

    @Test
    public void simpleHBase(){
        // create table
        if (!hBaseAdminDao.existsDatabase(storeURL, GRAPH)){
            hBaseAdminDao.createDatabase(storeURL, GRAPH);
        }
        hBaseAdminDao.createTable(storeURL, GRAPH, TABLE, true);

        // insert row
        HBaseRows rows = new HBaseRows();
        Map<String, String> row = new HashMap<>();
        row.put(Keys._ROW_KEY, "1");
        row.put("id", "1");
        row.put("name", "name1");
        rows.addStringRow(row);
        hBaseAdminDao.bulkUpsert(storeURL, GRAPH, TABLE, rows);

        // query row
        HBaseQuery query = new HBaseQuery(GRAPH);
        query.addRowKeys(TABLE, "1");
        HBaseQueryResult result = hBaseQueryDao.searchByKeys(storeURL,query);
        JSONUtils.println(result);
    }

    @Test
    public void listHBaseTables(){
        List<String> tables = hBaseAdminDao.listTableNames(storeURL, "demo.*");
        JSONUtils.println(tables);
    }

    @Test
    public void simpleHive(){
        HiveHelper hiveHelper = new HiveHelper(Resource.getActiveProfile());
        String sql = "select * from test_graph.test_table limit 10";
        //String sql = "show databases";
        List<Map<String, Object>> rows = hiveHelper.executeQuery(sql);
        JSONUtils.println(rows);
    }

    @Test
    public void simpleCreateHiveTable(){
        // create hive external table on hbase
        String sql = "CREATE DATABASE IF NOT EXISTS test_graph;\n" +
                "USE test_graph;\n" +
                "DROP TABLE IF EXISTS test_table;\n" +
                "CREATE EXTERNAL TABLE IF NOT EXISTS test_table(key STRING,id INT,name string) \n" +
                "STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' \n" +
                "WITH SERDEPROPERTIES ('hbase.columns.mapping'=':key,objects:id,objects:name') \n" +
                "TBLPROPERTIES('hbase.table.name'='test_graph:test_table','hbase.mapred.output" +
                ".outputtable'='test_graph:test_table')\n";
        List<String> sqlList = new ArrayList<>();
        for (String str : sql.split(";")) {
            sqlList.add(str);
        }
        HiveHelper hiveHelper = new HiveHelper(Resource.getActiveProfile());
        boolean success = hiveHelper.execute(sqlList);
        System.out.println(success);
    }

    @Test
    public void createHBaseDB(){
        hBaseAdminDao.createDatabase(storeURL,"crm_dev2");
    }

    @Test
    public void cleanup(){
        // drop table
        if (hBaseAdminDao.existsTable(storeURL, GRAPH, TABLE)){
            hBaseAdminDao.deleteTable(storeURL, GRAPH, TABLE);
        }
    }
}
