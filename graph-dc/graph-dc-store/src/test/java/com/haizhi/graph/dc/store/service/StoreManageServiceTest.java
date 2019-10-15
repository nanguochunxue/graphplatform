package com.haizhi.graph.dc.store.service;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.model.PageQo;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.qo.DcStoreQo;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.hdfs.HDFSHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengangxiong on 2019/03/26
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class StoreManageServiceTest {

    @Autowired
    private StoreManageService storeManageService;

    @Autowired
    private StoreUsageService storeUsageService;

    @Test
    public void test(){
        DcStoreQo qo = new DcStoreQo();
        PageQo pageQo = new PageQo();
        pageQo.setPageNo(1);
        pageQo.setPageSize(5);
        qo.setPage(pageQo);
        PageResponse res = storeManageService.findPage(qo);
        System.out.println(res);
    }

    @Test
    public void test2(){
        Response res = storeManageService.supportedVersion(StoreType.ES);
        System.out.println(res);
    }

    @Test
    public void testHDFSHelper(){
//        StoreURL storeURL = storeUsageService.findStoreURL("fi_graph", StoreType.HDFS);
        StoreURL storeURL = storeUsageService.findStoreURL(1000018L);
        HDFSHelper hdfsHelper = new HDFSHelper(storeURL);
//        System.out.println(hdfsHelper.upload("/Users/haizhi/hello.csv", "/user/dmp2/test"));
        System.out.println(hdfsHelper.exsits("/user/dmp2/test/hello.csv"));
    }
}