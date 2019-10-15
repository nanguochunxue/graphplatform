package com.haizhi.graph.server.es.search.builder;

import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.server.api.constant.EKeys;
import com.haizhi.graph.server.api.es.search.EsQuery;
import com.haizhi.graph.server.api.constant.QType;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by tanghaiyang on 2019/5/6.
 */
public class KeywordBuilder {

    public static QueryBuilder get(EsQuery esQuery) {
        BoolQueryBuilder keywordBuilder = QueryBuilders.boolQuery();
        String keyword = esQuery.getKeyword();
        if (StringUtils.isBlank(keyword)) {
            return keywordBuilder;
        }
        List<Map<String, Object>> query = esQuery.getQuery();
        for(Map<String, Object> schemaQuery: query) {
            float boost = Float.valueOf(schemaQuery.get(EKeys.boost).toString());
            String field = Getter.get(EKeys.field, schemaQuery);
            String operator = schemaQuery.get(EKeys.operator).toString().toLowerCase();
            MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(keyword);
            multiMatchQuery.boost(boost).tieBreaker(EKeys.tieBreaker);
            multiMatchQuery.field(field, boost);
            keywordBuilder.should(multiMatchQuery);
            QueryBuilder queryBuilder = null;
            if (operator.equals(QType.MATCH.name())) {
                queryBuilder = QueryBuilders
                        .matchPhraseQuery(field, keyword)
                        .slop(3)
                        .boost(boost);
            } else if (operator.equals(QType.EXISTS.name())) {
                queryBuilder = QueryBuilders
                        .existsQuery(field)
                        .boost(boost);
            }
            if(Objects.nonNull(queryBuilder)) keywordBuilder.should(queryBuilder);
            // wildcard search maybe slow
           /* String pattern = "*" + keyword + "*";
            WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(field, pattern);
            keywordBuilder.should(wildcardQueryBuilder);*/
        }
        return keywordBuilder;
    }
}
