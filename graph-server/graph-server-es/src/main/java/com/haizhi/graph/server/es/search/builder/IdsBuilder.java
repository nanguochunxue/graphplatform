package com.haizhi.graph.server.es.search.builder;

import com.haizhi.graph.server.api.es.search.EsQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Map;
import java.util.Set;

/**
 * Created by tanghaiyang on 2019/5/6.
 */
public class IdsBuilder {

    public static QueryBuilder get(EsQuery esQuery) {
        BoolQueryBuilder idsBuilder = QueryBuilders.boolQuery();
        Map<String, Set<String>> schemas = esQuery.getSchemaKeys();
        for (Map.Entry<String, Set<String>> entry : schemas.entrySet()) {
            IdsQueryBuilder idsQueryBuilder = QueryBuilders.idsQuery(entry.getKey());
            idsQueryBuilder.addIds(entry.getValue().toArray(new String[]{}));
            idsBuilder.should(idsQueryBuilder);
        }
        return idsBuilder;
    }
}
