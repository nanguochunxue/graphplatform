package com.haizhi.graph.server.es.search;


import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.constant.ServerEsStatus;
import com.haizhi.graph.server.api.es.search.EsProxy;
import com.haizhi.graph.server.api.es.search.EsQuery;
import com.haizhi.graph.server.api.es.search.EsQueryResult;
import com.haizhi.graph.server.api.es.search.EsSearchDao;
import com.haizhi.graph.server.es.client.EsClient;
import com.haizhi.graph.server.es.search.builder.AggBuilder;
import com.haizhi.graph.server.es.search.builder.RootBuilder;
import com.haizhi.graph.server.es.search.result.AggResultHandler;
import com.haizhi.graph.server.es.search.result.ResultHandler;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by tanghaiyang on 2019/4/30.
 */
@Repository
public class EsSearchDaoImpl implements EsSearchDao {

    private static final GLog LOG = LogFactory.getLogger(EsSearchDaoImpl.class);

    @Autowired
    private EsClient esClient;

    private TransportClient getClient(StoreURL storeURL) {
        return esClient.getClient(storeURL);
    }

    @Override
    public EsQueryResult search(StoreURL storeURL, EsQuery esQuery) {
        if (esQuery == null) {
            LOG.error("It cannot search with a empty EsQuery.");
            return new EsQueryResult();
        }
        String queryDSL = "";
        try {
            // builder
            SearchRequestBuilder searchBuilder = this.createSearchBuilder(storeURL, esQuery);
            // log
            queryDSL = searchBuilder.toString();
            if (esQuery.isDebugEnabled()) {
                LOG.info("Query DSL:\n{0}", queryDSL);
            }
            return rawSearch(searchBuilder, esQuery);
        } catch (Exception e) {
            LOG.error("[{0}/{1}] search error, while query DSL:\n{2}\n", e, esQuery.getGraph(), esQuery.getSchemas(), queryDSL);
            throw new UnexpectedStatusException(ServerEsStatus.SEARCH_ERROR, e, e.getMessage());
        }
    }

    @Override
    public EsQueryResult searchByIds(StoreURL storeURL, EsQuery esQuery) {
        if (esQuery == null) {
            LOG.error("It cannot search with a empty EsQuery.");
            return new EsQueryResult();
        }
        String queryDSL = "";
        try {
            wrapEsQuery(esQuery);
            // builder
            SearchRequestBuilder searchBuilder = this.createIdsBuilder(storeURL, esQuery);
            // log
            queryDSL = searchBuilder.toString();
            if (esQuery.isDebugEnabled()) {
                LOG.info("Query DSL:\n{0}", queryDSL);
            }
            return rawSearch(searchBuilder, esQuery);
        } catch (Exception e) {
            LOG.error("[{0}/{1}] search error, while query DSL:\n{2}\n{3}", e,
                    esQuery.getGraph(), esQuery.getSchemas(), queryDSL, e.getMessage());
            throw new UnexpectedStatusException(ServerEsStatus.SEARCHBYIDS_ERROR, e, e.getMessage());
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
        return null;
    }

    @Override
    public Map<String, Object> executeProxy(StoreURL storeURL, EsProxy esProxy) {
        return null;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private SearchRequestBuilder createSearchBuilder(StoreURL storeURL, EsQuery esQuery) {
        SearchRequestBuilder searchBuilder = this.getClient(storeURL)
                .prepareSearch(esQuery.getGraph())
                .setQuery(RootBuilder.get(esQuery))
                .setFetchSource(true)
                .setFrom(esQuery.getPageNo())
                .setSize(esQuery.getPageSize());
        if(CollectionUtils.isNotEmpty(esQuery.getSchemas())){
            searchBuilder.setTypes(esQuery.getSchemas().toArray(new String[]{}));
        }
        this.addAggregations(esQuery, searchBuilder);     // aggregation
        this.addSortFields(esQuery, searchBuilder);       // sort
        this.addHighlightFields(esQuery, searchBuilder);  // highlight
        return searchBuilder;
    }

    private SearchRequestBuilder createIdsBuilder(StoreURL storeURL, EsQuery esQuery) {
        SearchRequestBuilder searchBuilder = this.getClient(storeURL)
                .prepareSearch(esQuery.getGraph())
                .setQuery(RootBuilder.getIds(esQuery))
                .setFetchSource(true)
                .setFrom(esQuery.getPageNo())
                .setSize(esQuery.getPageSize());
        if(CollectionUtils.isNotEmpty(esQuery.getSchemas())){
            searchBuilder.setTypes(esQuery.getSchemas().toArray(new String[]{}));
        }
        return searchBuilder;
    }

    private void wrapEsQuery(EsQuery esQuery) {
        int total = 0;
        Map<String, Set<String>> schemas = esQuery.getSchemaKeys();
        for (Set<String> set : schemas.values()) {
            total += set.size();
        }
        esQuery.setPageSize(total);
    }

    private EsQueryResult rawSearch(SearchRequestBuilder searchBuilder, EsQuery esQuery) {
        try {
            SearchResponse searchResponse = searchBuilder.get();
            EsQueryResult result = ResultHandler.getSearchResult(esQuery, searchResponse);
            AggResultHandler.setAggResult(result, esQuery, searchResponse);
            return result;
        } catch (Exception e) {
            LOG.error("[{0}/{1}] search error, while query DSL:\n{2}", e, esQuery.getGraph(), esQuery.getSchemas());
            throw new UnexpectedStatusException(ServerEsStatus.SEARCH_ERROR, e, e.getMessage());
        }
    }

    private SearchRequestBuilder addAggregations(EsQuery esQuery, SearchRequestBuilder builder) {
        List<AbstractAggregationBuilder> builders = AggBuilder.get(esQuery.getAggregation());
        for (int i = 0; i < builders.size(); i++) {
            // Consider the performance, Limit 20.
            if (i == 20) {
                break;
            }
            builder.addAggregation(builders.get(i));
        }
        return builder;
    }

    private void addSortFields(EsQuery esQuery, SearchRequestBuilder searchBuilder) {
        if (CollectionUtils.isEmpty(esQuery.getSort())) {
            return;
        }
        for (Map<String, Object> sort : esQuery.getSort()) {
            try {
                String sortStr = sort.getOrDefault("order", "asc").toString();
                SortOrder sortOrder = SortOrder.fromString(sortStr);
                searchBuilder.addSort(sort.get("field").toString(), sortOrder);
            }catch (Exception e){
                LOG.error("sort type [{0}] not correct, exception:\n{1}", JSON.toJSONString(sort,true), e.getMessage());
            }
        }
    }

    private void addHighlightFields(EsQuery esQuery, SearchRequestBuilder searchBuilder) {
        if (!esQuery.isHighlight()) {
            return;
        }
        HighlightBuilder hiBuilder = new HighlightBuilder();
        hiBuilder.preTags(esQuery.getHighlighterPreTags());
        hiBuilder.postTags(esQuery.getHighlighterPostTags());
        Set<String> excludedHLFields = esQuery.getExcludedHLFields();
        for (String field : esQuery.getFields()) {
            if(excludedHLFields.contains(field)){
                continue;
            }
            hiBuilder.field(field);
        }
        searchBuilder.highlighter(hiBuilder);
    }
}
