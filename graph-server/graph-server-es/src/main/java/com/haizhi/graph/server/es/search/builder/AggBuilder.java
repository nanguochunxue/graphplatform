package com.haizhi.graph.server.es.search.builder;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.server.api.constant.EKeys;
import com.haizhi.graph.server.api.constant.QType;
import com.haizhi.graph.server.api.constant.Stats;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;

import java.util.*;

/**
 * Created by tanghaiyang on 2019/5/6.
 */
public class AggBuilder {

    private static final GLog LOG = LogFactory.getLogger(AggBuilder.class);

    public static List<AbstractAggregationBuilder> get(List<Map<String, Object>> aggregations) {
        if (CollectionUtils.isEmpty(aggregations)) {
            return Collections.emptyList();
        }
        List<AbstractAggregationBuilder> builders = new ArrayList<>();
        aggregations.forEach(aggregation -> builders.add(createAggregation(aggregation)));
        return builders;
    }

    @SuppressWarnings("unchecked")
    private static AbstractAggregationBuilder createAggregation(Map<String, Object> aggregation) {
        String key = Getter.get(EKeys.key, aggregation);
        String field = Getter.get(EKeys.field, aggregation);
        String typeStr = Getter.get(EKeys.type, aggregation);
        try {
            QType type = QType.valueOf(typeStr.toUpperCase());   // may be throw IllegalArgumentException
            switch (type) {
                case TERM:
                    String statsStr = Getter.get(EKeys.stats, aggregation);
                    Stats stats = Stats.fromName(statsStr);
                    AbstractAggregationBuilder levelOne = null;
                    if (Objects.isNull(stats)) {
                        LOG.error("TermAggregation have no stats: \n{0}", JSON.toJSONString(aggregation, true));
                        break;
                    }
                    switch (stats) {
                        case COUNT:
                            int size = (int)aggregation.get("size");
                            levelOne = AggregationBuilders.terms(key).field(field).size(size);
                            break;
                        case MIN:
                            levelOne = AggregationBuilders.min(key).field(field);
                            break;
                        case MAX:
                            levelOne = AggregationBuilders.max(key).field(field);
                            break;
                        case AVG:
                            levelOne = AggregationBuilders.avg(key).field(field);
                            break;
                        case SUM:
                            levelOne = AggregationBuilders.sum(key).field(field);
                            break;
                        case STATS:
                            levelOne = AggregationBuilders.stats(key).field(field);
                            break;
                    }
                    return levelOne;
                case RANGE:
                    List<Map<String,Object>> ranges = (List<Map<String,Object>>)aggregation.get(EKeys.ranges);
                    RangeAggregationBuilder rangeBuilder = AggregationBuilders.range(key).field(field);
                    for (Map<String,Object> range : ranges) {
                        double from = (range.get(EKeys.from) == null ? 0 : Double.valueOf(range.get(EKeys.from).toString()));
                        double to = (range.get(EKeys.to) == null ? 0 : Double.valueOf(range.get(EKeys.to) .toString()));
                        rangeBuilder.addRange(range.getOrDefault(EKeys.rangeKey, "default").toString(), from, to);
                    }
                    return rangeBuilder;
            }
        } catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException("createAggregation type is not TERM or RANGE", e);
        }
        return null;
    }
}
