package com.haizhi.graph.dc.store.api.service;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.server.api.bean.StoreURL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

/**
 * Created by chengangxiong on 2019/04/15
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi-fi")
public class StoreUsageServiceTest {

    @Autowired
    private StoreUsageService storeUsageService;

    @Test
    public void test(){
        StoreURL esRes = storeUsageService.findStoreURL("graph_gap_hello", StoreType.ES);
        StoreURL hbaseRes = storeUsageService.findStoreURL("graph_gap_hello", StoreType.Hbase);
        StoreURL arangoRes = storeUsageService.findStoreURL("graph_gap_hello", StoreType.GDB);
        System.out.println(esRes);
        System.out.println(hbaseRes);
        System.out.println(arangoRes);
    }

    @Test
    public void test2() throws InterruptedException {
        StoreURL storeURL = storeUsageService.findStoreURL("demo_graph", StoreType.ES);
        JSONUtils.println(storeURL);
        storeURL = storeUsageService.findStoreURL("demo_graph", StoreType.ES);
        JSONUtils.println(storeURL);
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}