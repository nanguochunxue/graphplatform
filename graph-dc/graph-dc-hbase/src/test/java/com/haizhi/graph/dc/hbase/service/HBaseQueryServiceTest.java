package com.haizhi.graph.dc.hbase.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.search.api.model.qo.KeySearchQo;
import com.haizhi.graph.search.api.model.vo.KeySearchVo;
import com.haizhi.graph.server.api.bean.StoreURL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by tanghaiyang on 2019/5/13.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class HBaseQueryServiceTest {

    private static final GLog LOG = LogFactory.getLogger(HBaseQueryServiceTest.class);

    @Autowired
    private StoreUsageService storeUsageService;

    @Autowired
    private HBaseQueryService hBaseQueryService;

    @Test
    public void getStoreUrl(){
        StoreURL storeURL = storeUsageService.findStoreURL("hbase_store", StoreType.Hbase);
        JSONUtils.println(storeURL);
    }

    @Test
    public void searchByKeys(){
        String condition = FileUtils.readTxtFile("api/searchByKeys_FI.json");
        KeySearchQo keySearchQo = JSONObject.parseObject(condition, KeySearchQo.class);
        LOG.info("keySearchQo: {0}",JSON.toJSONString(keySearchQo, true));
        Response<KeySearchVo> response = hBaseQueryService.searchByKeys(keySearchQo);
        LOG.info("KeySearchVo: {0}", JSON.toJSONString(response, true));
    }


}
