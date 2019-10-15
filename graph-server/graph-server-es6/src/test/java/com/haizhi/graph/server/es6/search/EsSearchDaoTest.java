package com.haizhi.graph.server.es6.search;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.es.search.EsProxy;
import com.haizhi.graph.server.api.es.search.EsQuery;
import com.haizhi.graph.server.api.es.search.EsQueryResult;
import com.haizhi.graph.server.api.es.search.EsSearchDao;
import com.haizhi.graph.server.es6.StoreURLFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by tanghaiyang on 2019/5/10.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class EsSearchDaoTest {

    private static final GLog LOG = LogFactory.getLogger(EsSearchDaoTest.class);
    private static final String index = "gap_fi_test.company";
    private static final String type = "demo_vertex";

    @Autowired
    private EsSearchDao esSearchDao;

    private StoreURL storeURL;

    @Before
    public void init() {
//        storeURL = StoreURLFactory.createEs();
        storeURL = StoreURLFactory.createEs_FIC80();
    }

    @Test
    public void search() {
//        String api = FileUtils.readTxtFile("api/search.json");
//        String api = FileUtils.readTxtFile("api/search2.json");
//        String api = FileUtils.readTxtFile("api/esQuery3.json");
        String api = FileUtils.readTxtFile("api/searchQo_testFiledType.json");
        EsQuery esQuery = JSON.parseObject(api, EsQuery.class);
        esQuery.setDebugEnabled(true);
        EsQueryResult esQueryResult = esSearchDao.search(storeURL, esQuery);
        LOG.info("esQueryResult: {0}", JSON.toJSONString(esQueryResult, true));
    }

    @Test
    public void search2() {
        EsQuery esQuery = new EsQuery();
        esQuery.setGraph("test_es.999.error");
        esQuery.setSchemas(Sets.newHashSet("test_333_error_999"));
        esQuery.setDebugEnabled(true);
        EsQueryResult esQueryResult = esSearchDao.search(storeURL, esQuery);
        JSONUtils.println(esQueryResult);
    }

    @Test
    public void search3() {
        String api = FileUtils.readTxtFile("api/search3.json");
        EsQuery esQuery = JSON.parseObject(api, EsQuery.class);
        esQuery.setDebugEnabled(true);
        EsQueryResult esQueryResult = esSearchDao.search(storeURL, esQuery);
        LOG.info("esQueryResult: {0}", JSON.toJSONString(esQueryResult, true));
    }

    @Test
    public void searchByIds() {
        String api = FileUtils.readTxtFile("api/findByIds.json");
        EsQuery esQuery = JSON.parseObject(api, EsQuery.class);
        esQuery.setDebugEnabled(true);
        EsQueryResult esQueryResult = esSearchDao.searchByIds(storeURL, esQuery);
        LOG.info("esQueryResult: {0}", JSON.toJSONString(esQueryResult, true));
    }

    @Test
    public void searchByDSL() {
        String api = FileUtils.readTxtFile("api/searchByDSLMatchAll.json");
        EsQuery esQuery = new EsQuery();
        esQuery.setPageSize(200);
        esQuery.setGraph(index);
        esQuery.setQueryDSL(api);
        esQuery.setDebugEnabled(true);
        EsQueryResult esQueryResult = esSearchDao.searchByDSL(storeURL, esQuery);
        LOG.info("esQueryResult: {0}", JSON.toJSONString(esQueryResult,true) );

//        esQuery.setGraph("test_es.999.error");
//        esQuery.setQueryDSL(api);
//        esQuery.setDebugEnabled(true);
//        EsQueryResult esQueryResult2 = esSearchDao.searchByDSL(storeURL, esQuery);
//        JSONUtils.println(esQueryResult2);
    }

    @Test
    public void searchByDSL3() {
//        String api = FileUtils.readTxtFile("api/searchByDSL3.json");
//        String api = FileUtils.readTxtFile("api/searchByDSL4.json");
//        String api = FileUtils.readTxtFile("api/searchByDSL5.json");
        String api = FileUtils.readTxtFile("api/searchByDSL_match_phrase.json");

        EsQuery esQuery = new EsQuery();
//        esQuery.setGraph("test_zmb.test_schema");
        esQuery.setGraph(index);
        esQuery.setQueryDSL(api);
        esQuery.setDebugEnabled(true);
        EsQueryResult esQueryResult = esSearchDao.searchByDSL(storeURL, esQuery);
        LOG.info("esQueryResult: {0}", JSON.toJSONString(esQueryResult,true) );
    }

    @Test
    public void executeProxy() {
//        String request = FileUtils.readTxtFile("api/esProxy1.json");
//        String request = FileUtils.readTxtFile("api/esProxy2.json");
//        String request = FileUtils.readTxtFile("api/esProxy_heath.json");
        String request = FileUtils.readTxtFile("api/esProxy_mappings.json");
        EsProxy esProxy = JSON.parseObject(request, EsProxy.class);
        LOG.info("esProxy:\n{0}", JSON.toJSONString(esProxy,true));
        Object esQueryResult = esSearchDao.executeProxy(storeURL, esProxy);
        JSONUtils.println(esQueryResult);
    }

}

