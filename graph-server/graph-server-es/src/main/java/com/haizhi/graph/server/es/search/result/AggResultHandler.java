package com.haizhi.graph.server.es.search.result;

import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.constant.EKeys;
import com.haizhi.graph.server.api.es.search.EsQuery;
import com.haizhi.graph.server.api.es.search.EsQueryResult;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.action.search.SearchResponse;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by tanghaiyang on 2019/7/18.
 */
public class AggResultHandler {

    private static final GLog LOG = LogFactory.getLogger(AggResultHandler.class);

    public static void setAggResult(EsQueryResult result, EsQuery esQuery, SearchResponse searchResponse) {
        List<Map<String, Object>> aggregations = esQuery.getAggregation();
        JSONObject response = JSONObject.parseObject(searchResponse.toString());
        if (CollectionUtils.isEmpty(aggregations)|| Objects.isNull(response)) {
            LOG.audit("aggregations is empty or searchResponse is null!");
            return;
        }
        result.setAggData(response.getJSONObject(EKeys.aggregations));
    }

}
