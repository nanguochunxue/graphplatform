package com.haizhi.graph.server.es6.index;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.es.index.EsIndexDao;
import com.haizhi.graph.server.api.es.index.bean.Source;
import com.haizhi.graph.server.es6.StoreURLFactory;
import com.haizhi.graph.server.es6.client.EsRestClient;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static com.haizhi.graph.server.api.constant.RequestMethod.GET;
import static com.haizhi.graph.server.api.constant.RequestMethod.PUT;

/**
 * Created by tanghaiyang on 2019/5/9.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class EsIndexDaoTest {

    private static final GLog LOG = LogFactory.getLogger(EsIndexDaoTest.class);

    @Autowired
    private EsRestClient esRestClient;

    @Autowired
    private EsIndexDao esIndexDao;

    private StoreURL storeURL;
    private static final String index = "gap_fi_test.company";
    private static final String type = "Company";

    @Before
    public void init() {
        //storeURL = StoreURLFactory.createEs();
        storeURL = StoreURLFactory.createEs_FIC80();
//        storeURL = StoreURLFactory.createEs_KSYUN();
    }

    @Test
    public void testConnect() {
        esIndexDao.testConnect(storeURL);
    }

    @Test
    public void existsIndex() {
        boolean ret = esIndexDao.existsIndex(storeURL, index);
        LOG.info("existsIndex: {0}", ret);
    }

    @Test
    public void existsType() {
        boolean ret = esIndexDao.existsType(storeURL, index, type);
        LOG.info("existsType: {0}", ret);
    }

    @Test
    public void createIndex() {
        boolean ret = esIndexDao.createIndex(storeURL, index);
        LOG.info("createIndex: {0}", ret);
    }

    @Test
    public void deleteIndex() {
        boolean ret = esIndexDao.deleteIndex(storeURL, index);
        LOG.info("deleteIndex: {0}", ret);
    }

    @Test
    public void createType() {
        boolean ret = esIndexDao.createType(storeURL, index, type);
        LOG.info("createType: {0}", ret);
    }

    @Test
    public void bulkUpsert() {
        List<Source> sourceList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("object_key", "1000");
        map.put("demo_string_field", "中国人民银行股份有限公司");
        map.put("demo_long_field", "222222");
        map.put("demo_double_field", "884.33");
        map.put("demo_date_field", "2019-05-21");
        Source source = new Source(Getter.get("object_key", map));
        source.setSource(map);
        sourceList.add(source);
        CudResponse ret = esIndexDao.bulkUpsert(storeURL, index, type, sourceList);
        JSONUtils.println(ret);
    }

    @Test
    public void delete() {
        List<Source> sourceList = new ArrayList<>();
        //Source source = new Source("1000");
        //sourceList.add(source);
        sourceList.add(new Source("E0FBB3B3B658F4898972F6102F095EB8"));
        sourceList.add(new Source("200DF40A06265FE899CE920A304F323C"));
        CudResponse ret = esIndexDao.delete(storeURL, index, type, sourceList);
        JSONUtils.println(ret);
    }

    @Test
    public void esTokenExpiredTest() throws Exception {
        boolean ret = esIndexDao.existsIndex(storeURL, index);
        LOG.info("existsIndex: {0}", ret);
        Thread.sleep(1000000);
        LOG.info("================================");
         ret = esIndexDao.existsIndex(storeURL, index);
        LOG.info("existsIndex: {0}", ret);
    }

    @Test
    public void esClusterBlockReadOnlynlyTest() throws Exception{
        RestClient client = esRestClient.getClient(storeURL);
        String payload = FileUtils.readTxtFile("api/read_only_allow_delete.json");
        HttpEntity entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
        Response response = client.performRequest(PUT, "/_all/_settings", Collections.emptyMap(), entity);
        JSONObject searchResponse = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        LOG.info("searchResponse: {0}", JSON.toJSONString(searchResponse,true));
    }

    @Test
    public void esGetMappingTest() throws Exception{
        RestClient client = esRestClient.getClient(storeURL);
        Response response = client.performRequest(GET, "/" + index + "/_mapping", Collections.emptyMap());
        JSONObject searchResponse = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        LOG.info("searchResponse: {0}", JSON.toJSONString(searchResponse,true));
    }

    @Test
    public void esGetSettingsTest() throws Exception{
        RestClient client = esRestClient.getClient(storeURL);
        Response response = client.performRequest(GET, "/" + index + "/_settings", Collections.emptyMap());
        JSONObject searchResponse = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        LOG.info("searchResponse: {0}", JSON.toJSONString(searchResponse,true));
    }

    @Test
    public void deleteAndCreateType() throws Exception{
        deleteIndex();
        createIndex();
        createType();
        bulkUpsert();
        esGetMappingTest();
    }
}
