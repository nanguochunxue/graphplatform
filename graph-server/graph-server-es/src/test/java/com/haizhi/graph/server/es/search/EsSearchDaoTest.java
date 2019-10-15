package com.haizhi.graph.server.es.search;

import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.es.search.EsQuery;
import com.haizhi.graph.server.api.es.search.EsQueryResult;
import com.haizhi.graph.server.api.es.search.EsSearchDao;
import com.haizhi.graph.server.api.constant.QType;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tanghaiyang on 2019/5/6.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class EsSearchDaoTest {

    private static final GLog LOG = LogFactory.getLogger(EsSearchDaoTest.class);

    @Autowired
    private EsSearchDao esSearchDAO;

    private StoreURL storeURL;
    private EsQuery esQuery;

    @Before
    public void init(){
        storeURL = new StoreURL("192.168.1.49:9300","root","");
    }

    @Test
    public void search(){
//        String searchQo = FileUtils.readTxtFile("api/esQuery.json");
//        String searchQo = FileUtils.readTxtFile("api/esQuery3.json");
        String searchQo = FileUtils.readTxtFile("api/esQuery_agg.json");
        esQuery = JSONObject.parseObject(searchQo, EsQuery.class);
        EsQueryResult result = esSearchDAO.search(storeURL, esQuery);
        LOG.info("result: {0}", JSONObject.toJSONString(result,true));
    }

    @Test
    public void searchByIds(){
        String searchQo = FileUtils.readTxtFile("api/searchByIds.json");
        esQuery = JSONObject.parseObject(searchQo, EsQuery.class);
        EsQueryResult result = esSearchDAO.searchByIds(storeURL, esQuery);
        LOG.info("result: {0}", JSONObject.toJSONString(result,true));
    }

    @Test
    public void searchWithAgg(){
        String searchQo = FileUtils.readTxtFile("api/esQuery.json");
        esQuery = JSONObject.parseObject(searchQo, EsQuery.class);
        EsQueryResult result = esSearchDAO.search(storeURL, esQuery);
        LOG.info("result: \n{0}", JSONObject.toJSONString(result,true));
    }

    @Test
    public void testSorter(){
        SortOrder sortOrder = SortOrder.DESC;
        SortOrder sortOrder2 = SortOrder.fromString("deSc1");
        SortOrder sortOrder3 = SortOrder.fromString("DESC");
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("order", "desc");
        sortMap.put("field", "openTime");
        Object o = sortMap.get("order");

        System.out.println(sortOrder2);
        System.out.println(sortOrder3);
        System.out.println(sortOrder);
        System.out.println(sortMap.get("order").toString().equals(sortOrder.toString()));
        System.out.println(sortMap.get("order").equals(sortOrder));
    }

    @Test
    public void testQType(){
        QType type = QType.valueOf("TERM");
        System.out.println(type);
    }
}
