package com.haizhi.graph.server.es6.search;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.es.index.EsIndexDao;
import com.haizhi.graph.server.api.es.index.bean.Source;
import com.haizhi.graph.server.api.es.search.EsQuery;
import com.haizhi.graph.server.api.es.search.EsQueryResult;
import com.haizhi.graph.server.api.es.search.EsSearchDao;
import com.haizhi.graph.server.es6.StoreURLFactory;
import com.haizhi.graph.server.es6.client.EsRestClient;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2019/6/21.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class EsExampleTest {
    private static final String INDEX = "demo_graph1.demo_vertex";
    private static final String TYPE = "demo_vertex";
    private static StoreURL storeURL;

    @Autowired
    EsRestClient esRestClient;
    @Autowired
    EsIndexDao esIndexDao;
    @Autowired
    EsSearchDao esSearchDao;

    @BeforeClass
    public static void init() {
//        storeURL = StoreURLFactory.createEs_FIC80();
//        storeURL = StoreURLFactory.createEs_KSYUN();
        storeURL = StoreURLFactory.createEs_FIC_release();
    }

    @Test
    public void simple() {
        createIndexIfNotExists();
        createTypeIfNotExists();
        getSettingAndMapping();
        bulkUpsert();
        search();
        searchByIds();
        searchByDSL();
        //deleteIndex();
    }

    @Test
    public void deleteIndex() {
        if (esIndexDao.existsIndex(storeURL, INDEX)) {
            esIndexDao.deleteIndex(storeURL, INDEX);
        }
        System.out.println("================================================");
    }

    @Test
    public void createIndexIfNotExists() {
        if (!esIndexDao.existsIndex(storeURL, INDEX)) {
            esIndexDao.createIndex(storeURL, INDEX);
        }
        System.out.println("================================================");
    }

    @Test
    public void createTypeIfNotExists() {
        if (!esIndexDao.existsType(storeURL, INDEX, TYPE)) {
            esIndexDao.createType(storeURL, INDEX, TYPE);
        }
        System.out.println("================================================");
    }

    @Test
    public void getSettingAndMapping() {
        String settings = esRestClient.getSetting(storeURL, INDEX);
        System.out.println(JSON.toJSONString(JSON.parseObject(settings), true));
        System.out.println("================================================");
        String mapping = esRestClient.getMapping(storeURL, INDEX);
        System.out.println(JSON.toJSONString(JSON.parseObject(mapping), true));
        System.out.println("================================================");
    }

    @Test
    public void bulkUpsert() {
        List<Source> sourceList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("object_key", "1000");
        map.put("demo_string_field", "华为技术有限公司");
        map.put("demo_string_field2", "华为技术有限公司");
        map.put("demo_string_field3", "广东");
        map.put("demo_long_field", 100);
        map.put("demo_double_field", 200.99);
        map.put("demo_date_field", "2019-05-21 18:45:56");
        Source source = new Source(Getter.get("object_key", map));
        source.setSource(map);
        sourceList.add(source);
        map = new HashMap<>();
        map.put("object_key", "1001");
        map.put("demo_string_field", "百度网络科技有限公司");
        map.put("demo_string_field2", "百度网络科技有限公司");
        map.put("demo_string_field3", "北京");
        map.put("demo_long_field", 101);
        map.put("demo_double_field", 201.99);
        map.put("demo_date_field", "2019-05-22");
        source = new Source(Getter.get("object_key", map));
        source.setSource(map);
        sourceList.add(source);
        CudResponse resp = esIndexDao.bulkUpsert(storeURL, INDEX, TYPE, sourceList);
        JSONUtils.println(resp);
        System.out.println("================================================");
    }

    @Test
    public void search() {
        String api = FileUtils.readTxtFile("api/search.json");
        EsQuery esQuery = JSON.parseObject(api, EsQuery.class);
        esQuery.setDebugEnabled(true);
        EsQueryResult result = esSearchDao.search(storeURL, esQuery);
        JSONUtils.println(result);
        System.out.println("================================================");
    }

    @Test
    public void searchByIds() {
        String api = FileUtils.readTxtFile("api/searchByIds.json");
        EsQuery esQuery = JSON.parseObject(api, EsQuery.class);
        esQuery.setDebugEnabled(true);
        EsQueryResult result = esSearchDao.searchByIds(storeURL, esQuery);
        JSONUtils.println(result);
        System.out.println("================================================");
    }

    @Test
    public void searchByDSL() {
        String api = FileUtils.readTxtFile("api/searchByDSLMatchAll.json");
        EsQuery esQuery = new EsQuery();
        esQuery.setGraph(INDEX);
        esQuery.setQueryDSL(api);
        esQuery.setDebugEnabled(true);
        EsQueryResult esQueryResult = esSearchDao.searchByDSL(storeURL, esQuery);
        JSONUtils.println(esQueryResult);
        System.out.println("================================================");
    }
}
