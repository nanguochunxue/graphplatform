package com.haizhi.graph.server.es.index.v1;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.es.index.EsIndexDao;
import com.haizhi.graph.server.api.es.index.bean.ScriptSource;
import com.haizhi.graph.server.api.es.index.bean.Source;
import com.haizhi.graph.server.api.es.index.bean.UpdateMode;
import com.haizhi.graph.server.api.es.search.EsQuery;
import com.haizhi.graph.server.api.es.search.EsSearchDao;
import com.haizhi.graph.server.es.Index;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * Created by zhengyang on 2019/05/13.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsIndexDAOTest {

    private StoreURL storeURL = new StoreURL("192.168.1.49:9300", "root", "");

    @Autowired
    EsIndexDao esIndexDAO;
    @Autowired
    EsSearchDao esSearchDAO;

    private static final String indexName = "es_tes";
    private static final String typeName = "es_test_person";

    @Test
    public void createType(){
//        String indexName = "es_test";
//        String typeName = "es_test_person";

        // create index if it not exists.
        Assert.assertTrue(esIndexDAO.createIndex(storeURL, indexName));
        // create type if it not exists.
        Assert.assertTrue(esIndexDAO.createType(storeURL, indexName, typeName));
    }

//    @Test
//    public void createTypeWithTemplate(){
//        Assert.assertTrue(esIndexDAO.createIndex(storeURL, indexName));
//        Assert.assertTrue(esIndexDAO.createType(storeURL, indexName, typeName));
//    }

    @Test
    public void deleteIndex(){
        Assert.assertTrue(esIndexDAO.deleteIndex(storeURL, indexName));
    }

    @Test
    public void bulkUpsert(){
        if (esIndexDAO.existsIndex(storeURL, indexName)){
            esIndexDAO.deleteIndex(storeURL, indexName);
        }
        esIndexDAO.createIndex(storeURL, indexName);
        esIndexDAO.createType(storeURL, indexName, typeName);
        List<Source> sourceList = new ArrayList<>();
        Source source = new Source("3");
        source.addField("date_field", "2018-01-10 11:32:48");
        source.addField("string_field", "string_value1");
        source.addField("long_field", 1234);
        source.addField("double_field", 123.0d);
        sourceList.add(source);

        System.out.println(JSON.toJSONString(sourceList));
        CudResponse cudResponse = esIndexDAO.bulkUpsert(storeURL, indexName, typeName, sourceList);
        JSONUtils.println(cudResponse);
//        Assert.assertTrue(esIndexDAO.bulkUpsert(storeURL, indexName, typeName, sourceList));
    }

    @Test
    public void bulkScripUpsert(){
        List<ScriptSource> sources = new ArrayList<>();
        ScriptSource source = new ScriptSource("1");
        // =
        source.updateNormalField("string", "stringFieldValue1");
        // +=
        source.updateNormalField("integer", 2, UpdateMode.INCREMENT);
        // -=
        source.updateNormalField("long", 5, UpdateMode.DECREMENT);
        sources.add(source);

        System.out.println(JSON.toJSONString(sources, true));
        CudResponse cudResponse = esIndexDAO.bulkScriptUpsert(storeURL, indexName, typeName, sources);
        JSONUtils.println(cudResponse);
//        Assert.assertTrue(esIndexDAO.bulkScriptUpsert(storeURL, indexName, typeName, sources));
    }

    @Test
    public void delete(){
        List<Source> sourceList = new ArrayList<>();
        Source source = new Source("3");
        sourceList.add(source);
        CudResponse cudResponse = esIndexDAO.delete(storeURL, indexName, typeName, sourceList);
        JSONUtils.println(cudResponse);

    }
}
