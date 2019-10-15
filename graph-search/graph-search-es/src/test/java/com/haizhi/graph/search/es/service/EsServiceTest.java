package com.haizhi.graph.search.es.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.search.api.es.service.EsService;
import com.haizhi.graph.search.api.model.qo.KeySearchQo;
import com.haizhi.graph.search.api.model.qo.NativeSearchQo;
import com.haizhi.graph.search.api.model.qo.SearchQo;
import com.haizhi.graph.search.api.model.vo.KeySearchVo;
import com.haizhi.graph.search.api.model.vo.NativeSearchVo;
import com.haizhi.graph.search.api.model.vo.SearchVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * Created by tanghaiyang on 2019/5/6.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class EsServiceTest {

    private static final GLog LOG = LogFactory.getLogger(EsServiceTest.class);

    @Autowired
    private EsService esService;

    @Test
    public void search() {
        // String api = FileUtils.readTxtFile("api/searchQo.json");
//        String api = FileUtils.readTxtFile("api/searchQo1.json");
//        String api = FileUtils.readTxtFile("api/searchQo_fi.json");
        String api = FileUtils.readTxtFile("api/searchQo_gap.json");
        SearchQo searchQo = JSONObject.parseObject(api, SearchQo.class);
        Response<SearchVo> response = esService.search(searchQo);
        JSONUtils.println(response);
    }

    @Test
    public void searchByKeys() {
        String condition = FileUtils.readTxtFile("api/searchByKeys.json");
        KeySearchQo keySearchQo = JSONObject.parseObject(condition, KeySearchQo.class);
        Response<KeySearchVo> response = esService.searchByKeys(keySearchQo);
        LOG.info("KeySearchVo: {0}", JSON.toJSONString(response, true));
    }

    @Test
    public void searchNative() {
        String api = FileUtils.readTxtFile("api/searchNative.json");
        Map<String, Object> queryDSL = JSONUtils.jsonToMap(api);
        NativeSearchQo searchQo = new NativeSearchQo();
        searchQo.setDebug(true);
        searchQo.setGraph("demo_graph");
        searchQo.setQuery(queryDSL);
        Response<NativeSearchVo> vo = esService.searchNative(searchQo);
        JSONUtils.println(vo);
    }

    @Test
    public void executeProxyTest(){
        GLog.AUDIT_ENABLED=true;
        String request = FileUtils.readTxtFile("api/executeProxy_plugins.txt");
        Response<Object> response = esService.executeProxy(request);
        JSONUtils.println(response);
    }

}
