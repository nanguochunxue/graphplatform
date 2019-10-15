package com.haizhi.graph.dc.store.api.redis;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.dc.core.dao.DcEnvFileDao;
import com.haizhi.graph.dc.core.dao.DcStoreDao;
import com.haizhi.graph.dc.core.model.po.DcEnvFilePo;
import com.haizhi.graph.dc.core.model.po.DcStorePo;
import com.haizhi.graph.dc.core.redis.DcStorePubService;
import com.haizhi.graph.dc.core.service.DcStoreService;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.server.api.bean.StoreURL;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Create by zhoumingbing on 2019-06-25
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcStorePubServiceTest {

    @Autowired
    private DcStorePubService dcStorePubService;

    @Autowired
    private StoreUsageService storeUsageService;

    @Autowired
    private DcStoreService dcStoreService;

    @Autowired
    private DcEnvFileDao dcEnvFileDao;

    @Autowired
    private DcStoreDao dcStoreDao;

    @Test
    public void publishByStoreId() throws InterruptedException {
        String graph = "test_interrupt";
        StoreType storeType = StoreType.Hbase;
        System.out.println("--------------修改之前获取-----------------");
        StoreURL storeURL = storeUsageService.findStoreURL(graph, storeType);
        System.out.println(JSON.toJSONString(storeURL, true));

        DcStorePo storePo = dcStoreService.findByNameAndType("error_hbase", storeType);
//        storePo.setUser("set_for_test_cache_un");
//        storePo.setPassword("set_for_test_cache_pw");
        storePo.setUser("");
        storePo.setPassword("");
        dcStoreDao.save(storePo);
        System.out.println("--------------修改之后获取-----------------");
        storeURL = storeUsageService.findStoreURL(graph, storeType);
        System.out.println(JSON.toJSONString(storeURL, true));

        dcStorePubService.publishByStoreId(storePo.getId());
        Thread.sleep(3000L);
        System.out.println("--------------通知更新缓存-----------------");
        storeURL = storeUsageService.findStoreURL(graph, storeType);
        System.out.println(JSON.toJSONString(storeURL, true));
    }


    @Test
    public void publishByEnvId() throws InterruptedException {
        String graph = "cache_refresh";
        StoreType es = StoreType.ES;
        StoreType hbase = StoreType.Hbase;
        Long envId = 2L;

        System.out.println("--------------修改之前获取ES-----------------");
        StoreURL esUrl = storeUsageService.findStoreURL(graph, es);
        System.out.println(JSON.toJSONString(esUrl, true));
        System.out.println("--------------修改之前获取Hbase----------------------");
        StoreURL hbaseUrl = storeUsageService.findStoreURL(graph, hbase);
        System.out.println(JSON.toJSONString(hbaseUrl, true));

        List<DcEnvFilePo> dcEnvFilePoList = dcEnvFileDao.findAllByEnvId(envId);
        dcEnvFilePoList.forEach(filePo -> {
            String[] split = StringUtils.split(filePo.getName(), ".");
            filePo.setName(split[0] + "(1)." + split[1]);
        });
        dcEnvFileDao.save(dcEnvFilePoList);

        System.out.println("--------------修改之后获取ES-----------------");
        esUrl = storeUsageService.findStoreURL(graph, es);
        System.out.println(JSON.toJSONString(esUrl, true));
        System.out.println("--------------修改之后获取Hbase----------------------");
        hbaseUrl = storeUsageService.findStoreURL(graph, hbase);
        System.out.println(JSON.toJSONString(hbaseUrl, true));

        dcStorePubService.publishByEnvId(envId);
        Thread.sleep(5000L);

        System.out.println("--------------通知更新之后获取ES-----------------");
        esUrl = storeUsageService.findStoreURL(graph, es);
        System.out.println(JSON.toJSONString(esUrl, true));
        System.out.println("--------------通知更新之后获取Hbase----------------------");
        hbaseUrl = storeUsageService.findStoreURL(graph, hbase);
        System.out.println(JSON.toJSONString(hbaseUrl, true));
    }
}
