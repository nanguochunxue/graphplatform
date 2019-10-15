package com.haizhi.graph.dc.es.client;

import com.haizhi.graph.common.context.Resource;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.es.index.EsIndexDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by tanghaiyang on 2019/5/9.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi-ksyun")
public class EsIndexDaoTest {

    @Autowired
    private EsIndexDao esIndexDao;

    @Test
    public void testConnectEs(){
        // change profiles => haizhi
        StoreURL storeURL = new StoreURL();
        storeURL.setUrl("192.168.1.49:9300,192.168.1.51:9300,192.168.1.52:9300");
        esIndexDao.testConnect(storeURL);
    }

    @Test
    public void testConnectEs6(){
        // change profiles => haizhi-fi
        StoreURL storeURL = new StoreURL();
        storeURL.setUrl("192.168.1.223:24148,192.168.1.224:24148,192.168.1.225:24148");
        storeURL.getFilePath().put("krb5.conf", Resource.getResourcePath("/fi/krb5.conf"));
        storeURL.getFilePath().put("jaas.conf", Resource.getResourcePath("/fi/jaas.conf"));
        storeURL.getFilePath().put("user.keytab", Resource.getResourcePath("/fi/user.keytab"));
        storeURL.setUserPrincipal("dmp");
        storeURL.setSecurityEnabled(true);
        esIndexDao.testConnect(storeURL);
    }

    @Test
    public void testConnectEs6KSYUN(){
        // change profiles => haizhi-ksyun
        StoreURL storeURL = new StoreURL();
        storeURL.setUrl("192.168.1.190:9200");
        esIndexDao.testConnect(storeURL);
    }
}
