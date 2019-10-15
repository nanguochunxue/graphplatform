package com.haizhi.graph.dc.hbase.dao;

import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.dc.hbase.StoreURLFactory;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.admin.HBaseAdminDao;
import com.haizhi.graph.server.api.hbase.admin.bean.HBaseRows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengangxiong on 2019/05/12
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class HBaseFiTest {

    @Autowired
    private StoreUsageService storeUsageService;

    @Autowired
    private HBaseAdminDao adminDAO;

    public static final String DATA_BASE = "testdatabase";

    public static final String TABLE = "testtable";

    private StoreURL storeURL;

    @Before
    public void init() {
        storeURL = StoreURLFactory.createHBase_FIC80();
    }

    @Test
    public void createDatabase(){
        boolean res = adminDAO.createDatabase(storeURL, DATA_BASE);
        assert res;
    }

    @Test
    public void createTable(){
        boolean table = adminDAO.createTable(storeURL, DATA_BASE, TABLE, false);
        assert table;
    }

    @Test
    public void listNamespace(){
        List<String> tableNames = adminDAO.listTableNames(storeURL, ".*");
        tableNames.forEach(tb -> {
            System.out.println(tb);
        });
    }

    @Test
    public void insert(){
        HBaseRows rows = new HBaseRows();
        Map<String, Object> d1 = new HashMap<>();
        d1.put("object_key", "e324234");
        rows.addRow(d1);
        CudResponse resp = adminDAO.bulkUpsert(storeURL, DATA_BASE, TABLE, rows);
        System.out.println(resp);
    }
}
