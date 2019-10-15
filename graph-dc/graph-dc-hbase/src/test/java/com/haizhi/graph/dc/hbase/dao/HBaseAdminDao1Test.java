package com.haizhi.graph.dc.hbase.dao;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.dc.hbase.StoreURLFactory;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.admin.HBaseAdminDao;
import com.haizhi.graph.server.api.hbase.admin.bean.HBaseRows;
import com.haizhi.graph.server.api.hbase.query.HBaseQueryDao;
import com.haizhi.graph.server.api.hbase.query.bean.HBaseQuery;
import com.haizhi.graph.server.hbase.client.HBaseClient;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/3/22.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi")
public class HBaseAdminDao1Test {

    static final String GRAPH = "test_graph";
    static final String TABLE = "test_table";
    static final String TABLE_NAME = GRAPH + ":" + TABLE;

    @Autowired
    HBaseAdminDao hBaseAdminDao;
    @Autowired
    HBaseQueryDao hBaseQueryDao;

    @Autowired
    private HBaseClient hBaseClient;

    private StoreURL storeURL;

    @Before
    public void init() {
        storeURL = StoreURLFactory.createHBase_FIC80();
    }

    @Test
    public void simpleTest() {
        // create table
        if (!hBaseAdminDao.existsDatabase(storeURL, GRAPH)) {
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
        JSONUtils.println(rows);
        hBaseAdminDao.bulkUpsert(storeURL, GRAPH, TABLE, rows);

        // query row
        HBaseQuery query = new HBaseQuery(GRAPH);
        query.addRowKeys(TABLE, "1");
    }

    @Test
    public void listTableNames(){
        List<String> results = hBaseAdminDao.listTableNames(storeURL, ".*.history");
        System.out.println(JSON.toJSONString(results, true));
    }

    @Test
    public void deleteByScan(){
        //hBaseAdminDao.delete("crm_dev", "Company", "016#0feb54", "016#0feb54~");
        hBaseAdminDao.deleteByScan(storeURL, "chengmo","test.user", "id-1|10", "id-1|10~");
    }

    @Test
    public void addAggCoprocessor(){
        hBaseAdminDao.addAggCoprocessor(storeURL, "crm_dev", "Company");
    }

    @Test
    public void connectFi() {
        Connection conn = hBaseClient.getConnection(storeURL);
        Admin admin = null;
        try {
            // Instantiate an Admin object.
            admin = conn.getAdmin();
            for ( NamespaceDescriptor ns : admin.listNamespaceDescriptors()){
                System.out.println(ns.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Create table failed.");
        } finally {
            if (admin != null) {
                try {
                    // Close the Admin object.
                    admin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Failed to close admin ");
                }
            }
        }
        System.out.println("Exiting testCreateTable.");
    }
}
