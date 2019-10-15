package com.haizhi.graph.server.hbase.query;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.query.HBaseQueryDao;
import com.haizhi.graph.server.api.hbase.query.bean.HBaseQuery;
import com.haizhi.graph.server.api.hbase.query.bean.HBaseQueryResult;
import com.haizhi.graph.server.hbase.StoreURLFactory;
import com.haizhi.graph.server.api.hbase.query.bean.HBaseRangeQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/2/5.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class HBaseQueryDaoTest {

    @Autowired
    HBaseQueryDao hBaseQueryDao;

    private StoreURL storeURL;

    @Before
    public void init() {
        storeURL = StoreURLFactory.createHBase_FIC80();
    }

    @Test
    public void getByRowKeys() {
        String tableName = "gap_test_db:invest";
        String rowKey = "594#200";
        List<Map<String, Object>> results = hBaseQueryDao.getByRowKeys(storeURL, tableName, rowKey);
        System.out.println(JSON.toJSONString(results, true));
    }

    @Test
    public void searchByKeys() {
        String tableName = "gap_test_db:test";
        String rowKey = "046#4AFA17AFDF37CA04C9B3841CD312D48F ";
        HBaseQuery hBaseQuery = new HBaseQuery();
        HBaseQueryResult results = hBaseQueryDao.searchByKeys(storeURL, hBaseQuery);
        System.out.println(JSON.toJSONString(results, true));
    }

    @Test
    public void rangeScanQuery() {
        HBaseRangeQuery rangeQuery = new HBaseRangeQuery("dev_zxl", "User");
        rangeQuery.setRange("255#2d3ae1f3af5fae834910edc4d775535c", "255#~");
        List<Map<String, String>> results = hBaseQueryDao.rangeScanQuery(storeURL, rangeQuery);
        System.out.println(JSON.toJSONString(results, true));
    }

}
