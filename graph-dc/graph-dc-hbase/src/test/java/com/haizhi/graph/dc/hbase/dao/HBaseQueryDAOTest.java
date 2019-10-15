package com.haizhi.graph.dc.hbase.dao;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.dc.hbase.StoreURLFactory;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.query.HBaseQueryDao;
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
public class HBaseQueryDAOTest {

    @Autowired
    HBaseQueryDao hBaseQueryDao;

    private StoreURL storeURL;

    @Before
    public void init() {
        storeURL = StoreURLFactory.createHBase_FIC80();
    }

    @Test
    public void getByRowKeys(){
        String tableName = "dev_zxl:User";
        String rowKey = "255#2d3ae1f3af5fae834910edc4d775535c";
        String url = "hbase://192.168.1.16,192.168.1.17,192.168.1.18:2181";
        List<Map<String, Object>> results = hBaseQueryDao.getByRowKeys(storeURL, tableName, rowKey);
        System.out.println(JSON.toJSONString(results, true));
    }

    /*
    * get 'graph_ccb_dev:te_repayment','006F52E9102A8D3BE2FE5614F42BA989', 'objects:pay_ccy'
    * */
    @Test
    public void getByRowKeysNew(){
        String tableName = "graph_ccb_dev:te_repayment";
        String rowKey = "006F52E9102A8D3BE2FE5614F42BA989";
        String url = "hbase://192.168.1.16,192.168.1.17,192.168.1.18:2181";
        List<Map<String, Object>> results = hBaseQueryDao.getByRowKeys(storeURL, tableName, url, rowKey);
        System.out.println(JSON.toJSONString(results, true));
    }
}
