package com.haizhi.graph.server.es.search.builder;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.server.api.constant.GOperator;
import com.haizhi.graph.server.api.constant.LogicOperator;
import com.haizhi.graph.server.api.constant.EKeys;
import com.haizhi.graph.server.api.es.search.EsQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by tanghaiyang on 2019/5/6.
 */
public class FilterBuilder {

    private static final GLog LOG = LogFactory.getLogger(FilterBuilder.class);

    public static QueryBuilder get(EsQuery esQuery) {
        Map<String, Object> filter = esQuery.getFilter();
        return buildFilterTree(filter);
    }

    @SuppressWarnings("unchecked")
    private static BoolQueryBuilder buildFilterTree(Map<String, Object> filter) {
        BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery();
        if (Objects.nonNull(filter) && filter.containsKey(EKeys.logicOperator) && filter.containsKey(EKeys.rules)) {
            String logicOperatorStr = filter.get(EKeys.logicOperator).toString();
            List<Map<String, Object>> ruleList = (List<Map<String, Object>>) filter.get(EKeys.rules);
            for (Map<String, Object> rule : ruleList) {
                BoolQueryBuilder ruleBuilder = buildRule(rule);
                if (Objects.isNull(ruleBuilder) || !ruleBuilder.hasClauses()) {
                    LOG.audit("ruleBuilder is null or has no clause");
                    continue;
                }
                LogicOperator logicOperator = LogicOperator.byValue(logicOperatorStr.toUpperCase());
                if (Objects.isNull(logicOperator)) {
                    LOG.audit("logicOperator is null");
                    continue;
                }
                switch (logicOperator) {
                    case AND:
                        filterBuilder.must(ruleBuilder);
                        break;
                    case OR:
                        filterBuilder.should(ruleBuilder);
                }
            }
        }
        return filterBuilder;
    }

    @SuppressWarnings("unchecked")
    private static BoolQueryBuilder buildRule(Map<String, Object> rule) {
        if (Objects.nonNull(rule) && rule.containsKey(EKeys.logicOperator) && rule.containsKey(EKeys.rules)) {
            return buildFilterTree(rule);
        } else if (rule.containsKey(EKeys.operator)) {
            try {
                String operator = rule.get(EKeys.operator).toString();
                String field = rule.get(EKeys.field).toString();
                Object value = rule.get(EKeys.value);
                FieldType fieldType = FieldType.fromCode(Getter.get(EKeys.fieldType, rule));
                BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery();
                GOperator gOperator = GOperator.byValue(operator.toUpperCase());
                if (Objects.isNull(gOperator)) {
                    return filterBuilder;
                }
                QueryBuilder childQueryBuilder;
                switch (gOperator) {
                    case EQ:
                        childQueryBuilder = QueryBuilders.termQuery(wrapField(field), value);
                        filterBuilder.must(childQueryBuilder);
                        break;
                    case NOT_EQ:
                        childQueryBuilder = QueryBuilders.termQuery(wrapField(field), value);
                        filterBuilder.mustNot(childQueryBuilder);
                        break;
                    case GT:
                        childQueryBuilder = QueryBuilders.rangeQuery(field).gt(value);
                        filterBuilder.must(childQueryBuilder);
                        break;
                    case GTE:
                        childQueryBuilder = QueryBuilders.rangeQuery(field).gte(value);
                        filterBuilder.must(childQueryBuilder);
                        break;
                    case LT:
                        childQueryBuilder = QueryBuilders.rangeQuery(field).lt(value);
                        filterBuilder.must(childQueryBuilder);
                        break;
                    case LTE:
                        childQueryBuilder = QueryBuilders.rangeQuery(field).lte(value);
                        filterBuilder.must(childQueryBuilder);
                        break;
                    case IN:
                        List<String> termsObject = (List<String>) value;
                        childQueryBuilder = QueryBuilders.termsQuery(wrapField(field), termsObject);
                        filterBuilder.must(childQueryBuilder);
                        break;
                    case NOT_IN:
                        childQueryBuilder = QueryBuilders.termsQuery(wrapField(field), value);
                        filterBuilder.mustNot(childQueryBuilder);
                        break;
                    case IS_NULL:
                        break;
                    case IS_NOT_NULL:
                        break;
                    case RANGE:
                        Map<String, Object> rangeObject = (Map<String, Object>) value;
                        childQueryBuilder = QueryBuilders.rangeQuery(field)
                                .from(rangeObject.get(EKeys.from))
                                .includeLower(getBoolean(rangeObject.get(EKeys.includeLower)))
                                .to(rangeObject.get(EKeys.to))
                                .includeUpper(getBoolean(rangeObject.get(EKeys.includeUpper)));
                        filterBuilder.must(childQueryBuilder);
                        break;
                    case NOT_RANGE:
                        Map<String, Object> notRangeObject = (Map<String, Object>) value;
                        childQueryBuilder = QueryBuilders.rangeQuery(field)
                                .from(notRangeObject.get(EKeys.from))
                                .includeLower(getBoolean(notRangeObject.get(EKeys.includeLower)))
                                .to(notRangeObject.get(EKeys.to))
                                .includeUpper(getBoolean(notRangeObject.get(EKeys.includeUpper)));
                        filterBuilder.mustNot(childQueryBuilder);
                        break;
                    case MATCH:
                        if (fieldType != FieldType.STRING){
                            LOG.audit("MATCH failed, cause fieldType is not string: {0}", fieldType);
                            break;
                        }
                        childQueryBuilder = QueryBuilders.matchPhraseQuery(field, value.toString()).slop(3).boost(1);
                        filterBuilder.must(childQueryBuilder);
                        break;
                }
                return filterBuilder;
            } catch (Exception e) {
                LOG.audit("buildRule fail, rule:\n{0}\nexception:\n{0}", JSON.toJSONString(rule,true), e.getMessage());
            }
        }
        return null;
    }

    private static boolean getBoolean(Object value) {
        return !(value instanceof Boolean) || (boolean) value;
    }

    private static String wrapField(String field) {
        return field;
    }

}
