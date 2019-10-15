package com.haizhi.graph.server.hbase.admin;

import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.admin.HBaseAdminDao;
import com.haizhi.graph.server.hbase.StoreURLFactory;
import com.haizhi.graph.server.api.hbase.admin.bean.HBaseRows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tanghaiyang on 2019/6/28.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class HBaseAdminDaoFiTest {

    @Autowired
    HBaseAdminDao hBaseAdminDao;

    private StoreURL storeURL;

    @Before
    public void init() {
        storeURL = StoreURLFactory.createHBase_FIC80();
    }

    /*
    *  checke hbase in fi env:
    *  get 'gap_test_db:invest', '594#200'
    * */
    @Test
    public void bulkUpsert(){
        String database = "gap_test_db";
        String tableName = "invest";
        HBaseRows rows = new HBaseRows();
        Map<String, Object> row = new HashMap<>();
        row.put(Keys.OBJECT_KEY, "200");
        row.put("id", "1");
        row.put("name", "name1");
        row.put("count", 40000000000.00);
        rows.addRow(row);
        hBaseAdminDao.bulkUpsert(storeURL, database, tableName, rows);
    }

}
