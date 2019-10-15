package com.haizhi.graph.search.arango.service;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.search.arango.Application;
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
@SpringBootTest(classes = Application.class)
@ActiveProfiles(value = "")
public class StoreUsageServiceTest {

    @Autowired
    private StoreUsageService storeUsageService;

    @Test
    public void test(){
        StoreURL esRes = storeUsageService.findStoreURL("graph_gap_hello", StoreType.ES);
        StoreURL hbaseRes = storeUsageService.findStoreURL("graph_gap_hello", StoreType.Hbase);
        StoreURL arangoRes = storeUsageService.findStoreURL("test", StoreType.GDB);
        JSONUtils.println(esRes);
        JSONUtils.println(hbaseRes);
        JSONUtils.println(arangoRes);
    }

}