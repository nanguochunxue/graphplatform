package com.haizhi.graph.server.es.client.http;

import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.es.client.EsRestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by tanghaiyang on 2019/7/12.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class EsRestClientTest {

    @Autowired
    EsRestClient esRestClient;

    @Test
    public void getClient(){
        StoreURL storeURL = new StoreURL();
        storeURL.setUrl("192.168.1.49:9300,192.168.1.51:9300,192.168.1.52:9300");
        esRestClient.getClient(storeURL);
    }
}
