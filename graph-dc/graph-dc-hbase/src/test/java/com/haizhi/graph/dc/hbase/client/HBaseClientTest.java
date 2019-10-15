package com.haizhi.graph.dc.hbase.client;

import com.haizhi.graph.dc.hbase.StoreURLFactory;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.hbase.client.HBaseClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengmo on 2018/2/5.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi-fi")
public class HBaseClientTest {

    @Autowired
    HBaseClient hBaseClient;

    private StoreURL storeURL;

    @Before
    public void init() {
        storeURL = StoreURLFactory.createHBase_FIC80();
    }

    @Test
    public void testConnectHBase(){
        // change profiles => haizhi
        StoreURL storeURL = new StoreURL();
        storeURL.setUrl("hadoop01.sz.haizhi.com,hadoop02.sz.haizhi.com,hadoop03.sz.haizhi.com:2181");
        Assert.assertNotNull(hBaseClient.getConnection(storeURL));
    }

    @Test
    public void testConnectHBaseFi(){
        Assert.assertNotNull(hBaseClient.getConnection(storeURL));
    }
}
