package com.haizhi.graph.server.es6.client;

import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.es.index.EsIndexDao;
import com.haizhi.graph.server.es6.StoreURLFactory;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Collections;

/**
 * Created by tanghaiyang on 2019/5/9.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class EsRestClientTest {

    @Autowired
    private EsRestClient esRestClient;

    @Autowired
    private EsIndexDao esIndexDao;

    private StoreURL storeURL;

    @Before
    public void init(){
        //storeURL = StoreURLFactory.createEs();
        storeURL = StoreURLFactory.createEs_FIC80();
    }

    @Test
    public void testConnect() throws IOException {
        esIndexDao.testConnect(storeURL);
    }

    @Test
    public void getClient() throws IOException {
        esRestClient.getClient(storeURL);
        esRestClient.getRestHighLevelClient(storeURL);
    }

    @Test
    public void tokenValidityTimeout() throws Exception {
        RestClient client = esRestClient.getClient(storeURL);
        Response response = client.performRequest("GET", "_cluster/health", Collections.emptyMap());
        System.out.println(response.getStatusLine().getStatusCode());
        JSONUtils.println(response);
        Thread.sleep(1000*330);

        // after 130s request again, set TOKEN_VALIDITY = 120s
        client.performRequest("GET", "_cluster/health", Collections.emptyMap());
        System.out.println(response.getStatusLine().getStatusCode());
        JSONUtils.println(response);
    }
}
