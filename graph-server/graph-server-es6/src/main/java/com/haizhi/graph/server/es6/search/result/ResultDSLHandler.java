package com.haizhi.graph.server.es6.search.result;

import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.constant.Fields;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.constant.EKeys;
import com.haizhi.graph.server.api.es.search.EsQueryResult;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghaiyang on 2019/6/10.
 */
public class ResultDSLHandler {

    private static final GLog LOG = LogFactory.getLogger(ResultDSLHandler.class);

    @SuppressWarnings("unchecked")
    public static EsQueryResult getSearchResult(Response response) {
        EsQueryResult queryResult = new EsQueryResult();
        try {
            JSONObject searchResponse = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
            JSONObject hitObject = searchResponse.getJSONObject(EKeys.hits);
            Long total = hitObject.getLongValue(EKeys.total);
            List<Map<String, Object>> sources = (List<Map<String, Object>>) hitObject.get(EKeys.hits);
            List<Map<String, Object>> rows = new ArrayList<>();
            for (Map<String, Object> source : sources) {
                Map<String, Object> row = (Map<String, Object>) source.getOrDefault(EKeys._source, new HashMap<>());
                row.put(Fields.ID, source.getOrDefault(Fields.ID, ""));
                row.put(Fields.SCORE, source.getOrDefault(Fields.SCORE, ""));
                rows.add(row);
            }
            queryResult.setRows(rows);
            queryResult.setTotal(total);
        }catch (Exception e){
            LOG.error(e);
        }
        return queryResult;
    }

}
