package com.haizhi.graph.server.arango.client;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.server.api.bean.StoreURL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengangxiong on 2019/04/15
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class ArangoClientTest {

    @Autowired
    private ArangoClient arangoClient;

    @Test
    public void getClient(){
        StoreURL storeURL = new StoreURL();
        storeURL.setUrl("192.168.1.36:8529,192.168.1.37:8529,192.168.1.38:8529");
        storeURL.setUser("");
        storeURL.setPassword("");
        ArangoDB client = arangoClient.getClient(storeURL);
        ArangoDatabase db = client.db("demo_graph");
        JSONUtils.println(db.getInfo());
    }

}