package com.haizhi.graph.server.es.client;

import com.haizhi.graph.server.api.bean.StoreURL;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengmo on 2017/12/4.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class EsClientTest {

    @Autowired
    EsClient esClient;

    @Test
    public void getClientByStoreURL(){
        StoreURL storeURL = new StoreURL();
        storeURL.setUrl("192.168.1.49:9300,192.168.1.51:9300,192.168.1.52:9300");
        Assert.assertNotNull(esClient.getClient(storeURL));
    }



}
