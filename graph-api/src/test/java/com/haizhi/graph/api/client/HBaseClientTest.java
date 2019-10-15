package com.haizhi.graph.api.client;

import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.hbase.client.HBaseClient;
import org.junit.Assert;
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

    @Test
    public void testConnectHBase(){
        // change profiles => haizhi
        StoreURL storeURL = new StoreURL();
        storeURL.setUrl("hadoop01.sz.haizhi.com,hadoop02.sz.haizhi.com,hadoop03.sz.haizhi.com:2181");
        Assert.assertNotNull(hBaseClient.getConnection(storeURL));
    }

    @Test
    public void testConnectHBaseFi(){
        // change profiles => haizhi-fi
        StoreURL storeURL = new StoreURL();
        storeURL.setUrl("192.168.1.223,192.168.1.224,192.168.1.225:24002");
        storeURL.getFilePath().put("core-site.xml", "/Users/haizhi/git/gp/graph/graph-api/src/test/resources/fi/core-site.xml");
        storeURL.getFilePath().put("hdfs-site.xml", "/Users/haizhi/git/gp/graph/graph-api/src/test/resources/fi/hdfs-site.xml");
        storeURL.getFilePath().put("hbase-site.xml", "/Users/haizhi/git/gp/graph/graph-api/src/test/resources/fi/hbase-site.xml");
        storeURL.getFilePath().put("krb5.conf", "/Users/haizhi/git/gp/graph/graph-api/src/test/resources/fi/krb5.conf");
        storeURL.getFilePath().put("user.keytab", "/Users/haizhi/git/gp/graph/graph-api/src/test/resources/fi/user.keytab");
        storeURL.setUserPrincipal("dmp");
        storeURL.setSecurityEnabled(true);
        Assert.assertNotNull(hBaseClient.getConnection(storeURL));
    }
}
