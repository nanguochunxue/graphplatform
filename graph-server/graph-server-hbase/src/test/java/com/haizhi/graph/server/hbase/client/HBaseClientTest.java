package com.haizhi.graph.server.hbase.client;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.hbase.StoreURLFactory;
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
@ActiveProfiles(profiles = "")
public class HBaseClientTest {

    private static final GLog LOG = LogFactory.getLogger(HBaseClientTest.class);

    @Autowired
    HBaseClient hBaseClient;

    private StoreURL storeURL;

    @Before
    public void init(){
        storeURL = StoreURLFactory.createHBase_FIC80();
    }


    @Test
    public void testConnect(){
//        StoreURL storeURL = new StoreURL();
//        storeURL.setUrl("192.168.1.223,192.168.1.224,192.168.1.225:24002");
//        storeURL.getFilePath().put("core-site.xml", Resource.getResourcePath("/fi/core-site.xml"));
//        storeURL.getFilePath().put("hdfs-site.xml", Resource.getResourcePath("/fi/hdfs-site.xml"));
//        storeURL.getFilePath().put("hbase-site.xml", Resource.getResourcePath("/fi/hbase-site.xml"));
//        storeURL.getFilePath().put("krb5.conf", Resource.getResourcePath("/fi/krb5.conf"));
//        storeURL.getFilePath().put("user.keytab", Resource.getResourcePath("/fi/user.keytab"));
//        storeURL.setUserPrincipal("dmp");
//        storeURL.setSecurityEnabled(true);
        LOG.info("storeURL: {0}", JSON.toJSONString(storeURL,true));
        Assert.assertNotNull(hBaseClient.getConnection(storeURL));
    }
}
