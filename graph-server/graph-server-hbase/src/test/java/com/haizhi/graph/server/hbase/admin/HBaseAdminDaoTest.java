package com.haizhi.graph.server.hbase.admin;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.admin.HBaseAdminDao;
import com.haizhi.graph.server.hbase.StoreURLFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by chengmo on 2018/3/22.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class HBaseAdminDaoTest {

    @Autowired
    HBaseAdminDao hBaseAdminDao;

    private StoreURL storeURL;

    @Before
    public void init() {
        storeURL = StoreURLFactory.createHBase_FIC80();
//        storeURL = StoreURLFactory.createHBase_KSYUN();
    }

    @Test
    public void listTableNames(){
//        List<String> results = hBaseAdminDao.listTableNames(storeURL, ".*.history");
        List<String> results = hBaseAdminDao.listTableNames(storeURL, "demo.*");
        System.out.println(JSON.toJSONString(results, true));
    }

    @Test
    public void deleteByScan(){
        //hBaseAdminDao.delete("crm_dev", "Company", "016#0feb54", "016#0feb54~");
        hBaseAdminDao.deleteByScan(storeURL, "chengmo:", "test.user", "id-1|10", "id-1|10~");
    }

    @Test
    public void addAggCoprocessor(){
        hBaseAdminDao.addAggCoprocessor(storeURL, "crm_dev", "Company");
    }
}
