package com.haizhi.graph.server.es6.search;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.constant.RequestMethod;
import com.haizhi.graph.server.api.core.EsBuilder;
import com.haizhi.graph.server.api.es.search.EsProxy;
import com.haizhi.graph.server.api.es.search.EsQuery;
import com.haizhi.graph.server.api.es.search.EsQueryResult;
import com.haizhi.graph.server.api.es.search.EsSearchDao;
import com.haizhi.graph.server.api.exception.EsServerException;
import com.haizhi.graph.server.es6.client.EsRestClient;
import com.haizhi.graph.server.es6.search.builder.IdsBuilder;
import com.haizhi.graph.server.es6.search.builder.RootBuilder;
import com.haizhi.graph.server.es6.search.result.ResultDSLHandler;
import com.haizhi.graph.server.es6.search.result.ResultHandler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Created by tanghaiyang on 2019/5/10.
 */
@Repository
public class EsSearchDaoImpl implements EsSearchDao {

    private static final GLog LOG = LogFactory.getLogger(EsSearchDaoImpl.class);

    @Autowired
    private EsRestClient esRestClient;

    @Override
    public EsQueryResult search(StoreURL storeURL, EsQuery esQuery) {
        if (CollectionUtils.isEmpty(esQuery.getSchemas())) {
            throw new EsServerException("schemas must not be empty");
        }
        SearchSourceBuilder sourceBuilder = this.createSearchBuilder(esQuery);
        try {
            Set<String> indices = new HashSet<>();
            if(Objects.nonNull( esQuery.getSchemas())){
                esQuery.getSchemas().forEach(type->indices.add(EsBuilder.getEs6Index(esQuery.getGraph(), type)));
            }
            return doSearch(storeURL, esQuery, sourceBuilder, indices);
        } catch (Exception e) {
            handleException(e, storeURL, esQuery, sourceBuilder);
            throw new EsServerException(e);
        }
    }

    @Override
    public EsQueryResult searchByIds(StoreURL storeURL, EsQuery esQuery) {
        if (MapUtils.isEmpty(esQuery.getSchemaKeys())) {
            LOG.audit("para parse failed, there is no schemaKeys: {0}", JSON.toJSONString(esQuery,true));
            return new EsQueryResult();
        }
        SearchSourceBuilder sourceBuilder = this.createIdsBuilder(esQuery);
        try {
            Set<String> indices = new HashSet<>();
            if(Objects.nonNull( esQuery.getSchemaKeys())){
                esQuery.getSchemaKeys().keySet().forEach(type->
                        indices.add(EsBuilder.getEs6Index(esQuery.getGraph(), type)));
            }
            return doSearch(storeURL, esQuery, sourceBuilder, indices);
        } catch (Exception e) {
            handleException(e, storeURL, esQuery, sourceBuilder);
            throw new EsServerException(e);
        }
    }

    @Override
    public EsQueryResult searchByFields(StoreURL storeURL, EsQuery esQuery) {
        return null;
    }

    @Override
    public List<EsQueryResult> multiSearch(StoreURL storeURL, List<EsQuery> list) {
        return null;
    }

    @Override
    public EsQueryResult searchByDSL(StoreURL storeURL, EsQuery esQuery) {
        try {
            String uri = String.format("/%s/_search", esQuery.getGraph());
//            String uri = "/_search";
            RestClient restClient = esRestClient.getClient(storeURL);
            HttpEntity entity = new NStringEntity(esQuery.getQueryDSL(), ContentType.APPLICATION_JSON);
            Response response = restClient.performRequest(RequestMethod.POST, uri, Collections.emptyMap(), entity);
            return ResultDSLHandler.getSearchResult(response);
        } catch (Exception e) {
            LOG.error("search error! \nstoreURL={0}, \nesQuery={1}",
                    e, JSON.toJSONString(storeURL), JSON.toJSONString(esQuery));
            throw new EsServerException(e);
        }
    }

    @Override
    public Object executeProxy(StoreURL storeURL, EsProxy esProxy) {
        String resultStr;
        try {
            RestClient restClient = esRestClient.getClient(storeURL);
            String content = esProxy.getContent();
            Response response;
            if(StringUtils.isEmpty(content)){
                response = restClient.performRequest(esProxy.getRequestMethod(), esProxy.getUri(), Collections.emptyMap());
            }else {
                HttpEntity entity = new NStringEntity(esProxy.getContent(), ContentType.APPLICATION_JSON);
                response = restClient.performRequest(esProxy.getRequestMethod(), esProxy.getUri(), Collections.emptyMap(), entity);
            }
            resultStr = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            LOG.error("executeProxy error! \nstoreURL={0}, \nrequest={1}", e,
                    JSON.toJSONString(storeURL), JSON.toJSONString(esProxy,true));
            throw new EsServerException(e);
        }
        return responseWrapper(resultStr);
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private EsQueryResult doSearch(StoreURL storeURL, EsQuery esQuery, SearchSourceBuilder sourceBuilder,
                                   Set<String> indices) throws Exception {
        LOG.audit("Query DSL:\n{0}", sourceBuilder.toString());
        SearchRequest searchRequest = new SearchRequest(indices.toArray(new String[]{})).source(sourceBuilder);
        SearchResponse searchResponse = esRestClient.getRestHighLevelClient(storeURL).search(searchRequest);
        return ResultHandler.getSearchResult(esQuery, searchResponse);
    }

    private void handleException(Exception e, StoreURL storeURL, EsQuery esQuery, SearchSourceBuilder sourceBuilder) {
        LOG.error("search error! \nstoreURL={0}, \nesQuery={1}, \n{2}",
                e, storeURL, esQuery, sourceBuilder.toString());
    }

    private Object responseWrapper(String resultStr){
        try {
            return JSONObject.parse(resultStr);
        }catch (Exception e){
            LOG.info("can not parse as Map<String,Object>");
            try {
                return JSONArray.parse(resultStr);
            }catch (Exception ee){
                LOG.info("can not parse as Map or List");
                return resultStr;
            }
        }
    }

    private SearchSourceBuilder createSearchBuilder(EsQuery esQuery) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(RootBuilder.get(esQuery))
                .fetchSource(true)
                .from(esQuery.getPageNo())
                .size(esQuery.getPageSize());

        return sourceBuilder;
    }

    private SearchSourceBuilder createIdsBuilder(EsQuery esQuery){
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(IdsBuilder.get(esQuery))
                .fetchSource(true)
                .from(esQuery.getPageNo())
                .size(esQuery.getPageSize());
        return sourceBuilder;
    }

}
