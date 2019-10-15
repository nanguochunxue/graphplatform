package com.haizhi.graph.server.es6.search.builder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.server.api.es.search.EsQuery;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Test;

/**
 * Created by tanghaiyang on 2019/6/10.
 */
public class IdsBuilderTest {

    private static final GLog LOG = LogFactory.getLogger(IdsBuilderTest.class);

    @Test
    public void test(){
        String api = FileUtils.readTxtFile("api/findByIds.json");
        EsQuery esQuery = JSON.parseObject(api, EsQuery.class);

        QueryBuilder queryBuilder = IdsBuilder.get(esQuery);

        LOG.info("idsBuilder: {0}", queryBuilder.toString());

        JSONObject queryDslObject = new JSONObject();
        queryDslObject.put("from", 0);
        queryDslObject.put("size", 5);

        String dsl = queryBuilder.toString().replaceAll("\r|\n", "");
        JSONObject dslObject = JSONObject.parseObject(dsl);
        queryDslObject.put("query", dslObject);

        LOG.info("dsl:{0}", JSON.toJSONString(queryDslObject,true));
        esQuery.setQueryDSL(JSON.toJSONString(queryDslObject,true));

        LOG.info("esQuery:\n{0}", JSON.toJSONString(esQuery,true));
    }


}
