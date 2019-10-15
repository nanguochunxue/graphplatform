package com.haizhi.graph.search.es.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.haizhi.graph.common.constant.Fields;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.search.api.es.service.EsService;
import com.haizhi.graph.search.api.model.qo.KeySearchQo;
import com.haizhi.graph.search.api.model.qo.NativeSearchQo;
import com.haizhi.graph.search.api.model.qo.SearchQo;
import com.haizhi.graph.search.api.model.vo.KeySearchVo;
import com.haizhi.graph.search.api.model.vo.NativeSearchVo;
import com.haizhi.graph.search.api.model.vo.SearchVo;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.constant.RequestMethod;
import com.haizhi.graph.server.api.constant.ServerEsStatus;
import com.haizhi.graph.server.api.es.search.EsProxy;
import com.haizhi.graph.server.api.es.search.EsQuery;
import com.haizhi.graph.server.api.es.search.EsQueryResult;
import com.haizhi.graph.server.api.es.search.EsSearchDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by chengmo on 2019/4/25.
 */
@Service
public class EsServiceImpl implements EsService {

    private static final GLog LOG = LogFactory.getLogger(EsServiceImpl.class);

    @Autowired
    private EsSearchDao esSearchDao;
    @Autowired
    private StoreUsageService storeUsageService;

    @Override
    @SuppressWarnings("unchecked")
    public Response<SearchVo> search(SearchQo searchQo) {
        LOG.audit("SearchQo:\n{0}", JSON.toJSONString(searchQo,true));
        try {
            String graph = searchQo.getGraph();
            StoreURL storeURL = storeUsageService.findStoreURL(graph, StoreType.ES);
            EsQuery esQuery = esQueryBuilder(searchQo);
            EsQueryResult result = esSearchDao.search(storeURL, esQuery);
            return Response.success(SearchVo.create(result));
        } catch (Exception e) {
            throw new UnexpectedStatusException(ServerEsStatus.SEARCH_FAIL, e, e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response<KeySearchVo> searchByKeys(KeySearchQo searchQo) {
        LOG.audit("KeySearchQo:\n{0}", JSON.toJSONString(searchQo,true));
        try {
            String graph = searchQo.getGraph();
            StoreURL storeURL = storeUsageService.findStoreURL(graph, StoreType.ES);
            EsQuery esQuery = new EsQuery();
            esQuery.setGraph(graph);
            esQuery.setSchemaKeys(searchQo.getSchemaKeys());
            EsQueryResult result = esSearchDao.searchByIds(storeURL, esQuery);

            Map<String, Object> dataMap = Maps.newHashMap();
            if(CollectionUtils.isEmpty(result.getRows())){
                LOG.audit("EsQueryResult rows is empty:\n{0}", result.getRows());
                return Response.success();
            }
            for (Map<String, Object> row : result.getRows()) {
                String schema = Getter.get(Fields.SCHEMA, row);
                if (!dataMap.containsKey(schema)){
                    List<Map<String, Object>> rows = new ArrayList<>();
                    rows.add(row);
                    dataMap.put(schema, rows);
                    continue;
                }
                List<Map<String, Object>> rows = Getter.getListMap(dataMap.get(schema));
                rows.add(row);
            }
            return Response.success(KeySearchVo.create(dataMap));
        } catch (Exception e) {
            throw new UnexpectedStatusException(ServerEsStatus.SEARCH_BY_KEYS_FAIL, e, e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response<NativeSearchVo> searchNative(NativeSearchQo searchQo) {
        LOG.audit("NativeSearchQo:\n{0}", JSON.toJSONString(searchQo,true));
        try {
            String graph = searchQo.getGraph();
            StoreURL storeURL = storeUsageService.findStoreURL(graph, StoreType.ES);
            EsQuery esQuery = new EsQuery();
            esQuery.setGraph(searchQo.getGraph());
            esQuery.setQueryDSL(JSON.toJSONString(searchQo.getQuery()));
            esQuery.setDebugEnabled(searchQo.isDebug());
            EsQueryResult result = esSearchDao.searchByDSL(storeURL, esQuery);
            return Response.success(NativeSearchVo.create(result));
        } catch (Exception e) {
            throw new UnexpectedStatusException(ServerEsStatus.SEARCH_NATIVE_FAIL, e, e.getMessage());
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public Response<Object> executeProxy(String request) {
        LOG.audit("request: \n{0}", request);
        try {
            EsProxy esProxy = esProxyBuilder(request);
            LOG.audit("esProxy:{0}", esProxy);
            if(Objects.isNull(esProxy)){
                return Response.error("para build failed, check the error log!");
            }
            String graph = esProxy.getGraph();
            StoreURL storeURL = storeUsageService.findStoreURL(graph, StoreType.ES);
            Object esProxyResult = esSearchDao.executeProxy(storeURL, esProxy);
            return Response.success(esProxyResult);
        } catch (Exception e) {
            throw new UnexpectedStatusException(ServerEsStatus.EXECUTE_PROXY_FAIL, e, e.getMessage());
        }
    }

    ///////////////////////////
    /////// private function
    ///////////////////////////
    private static EsQuery esQueryBuilder(SearchQo searchQo) {
        EsQuery esQuery = new EsQuery();
        esQuery.setGraph(searchQo.getGraph());
        esQuery.setKeyword(searchQo.getKeyword());
        esQuery.setSchemas(searchQo.getSchemas());
        esQuery.setFilter(searchQo.getFilter());
        esQuery.setQuery(searchQo.getQuery());
        esQuery.setPageNo(searchQo.getPageNo());
        esQuery.setPageSize(searchQo.getPageSize());
        esQuery.setFields(searchQo.getFields());
        esQuery.setSort(searchQo.getSort());
        esQuery.setHighlight(searchQo.isHighlight());
        esQuery.setAggregation(searchQo.getAggregation());
        esQuery.setDebugEnabled(searchQo.isDebug());
        return esQuery;
    }

    private static EsProxy esProxyBuilder(String request) {
        if (StringUtils.isEmpty(request)) {
            LOG.error("para parse fail, check para[request]!");
            return null;
        }
        EsProxy esProxy = new EsProxy();
        StringBuilder content = new StringBuilder();
        String[] body = request.split("\n");
        String graph = null;
        String[] headLine;
        String[] firstLine = body[0].trim().split(" ");
        if (firstLine.length == 1) {
            graph = firstLine[0];
            headLine = body[1].trim().split(" ");
        } else if (firstLine.length == 2) {
            headLine = firstLine;
        } else {
            LOG.error("para fail, check first line!");
            return null;
        }
        if (headLine.length != 2) {
            LOG.error("para head fail, check second line!");
            return null;
        }
        String requestMethod = headLine[0].trim();
        if (StringUtils.isEmpty(requestMethod)) {
            LOG.error("para Request Method fail, check first line!");
            return null;
        }
        if (!RequestMethod.AllMethod.contains(requestMethod)) {
            LOG.error("Request Method not support: {0}", request);
            return null;
        }
        String uri = headLine[1].trim();
        if(!uri.startsWith("/")){
            uri = "/" + uri;
        }
        String[] uriPathArr = uri.split("/");
        if(Objects.isNull(graph)) {
            String firstPathPara = uriPathArr[1];
            if (!firstPathPara.startsWith("_")) {
                graph = firstPathPara;
            }
        }
        if (StringUtils.isEmpty(uri)) {
            LOG.error("para uri fail, check first line!");
            return null;
        }
        for (int i = 2; i < body.length; i++) {
            content.append(body[i].trim());
        }
        if (StringUtils.isEmpty(graph)) {
            LOG.error("para graph fail, check first line!");
            return null;
        }
        esProxy.setGraph(graph);
        esProxy.setRequestMethod(requestMethod);
        esProxy.setUri(uri);
        esProxy.setContent(content.toString());
        return esProxy;
    }


}
