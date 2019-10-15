package com.haizhi.graph.server.es.search.result;

import com.haizhi.graph.common.constant.Fields;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.es.search.EsQuery;
import com.haizhi.graph.server.api.es.search.EsQueryResult;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/6/1.
 */
public class ResultHandler {

    private static final GLog LOG = LogFactory.getLogger(ResultHandler.class);

    /**
     * @param esQuery
     * @param searchResponse
     * @return
     */
    public static EsQueryResult getSearchResult(EsQuery esQuery, SearchResponse searchResponse) {
        EsQueryResult result = new EsQueryResult();
        try {
            List<Map<String, Object>> resultList = new ArrayList<>();

            boolean isHighlight = esQuery.isHighlight();
            SearchHits searchHits = searchResponse.getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> row = hit.getSourceAsMap();  // synchronise es6
                row.put(Fields.SCHEMA, hit.getType());
                row.put(Fields.ID, hit.getId());
                row.put(Fields.SCORE, hit.getScore());
                resultList.add(row);
                if (!isHighlight) {
                    continue;
                }
                Map<String, HighlightField> hiMap = hit.getHighlightFields();
                for(Map.Entry<String, HighlightField> entry :hiMap.entrySet()){
                    Object[] contents = entry.getValue().fragments();
                    if (contents.length == 1) {
                        row.put(entry.getKey(), contents[0].toString());
                    } else {
                        LOG.warn("The results of the highlight in the search results appear more data, fragments size = "
                                        + contents.length);
                    }
                }
            }
            result.setRows(resultList);
            result.setTotal(searchHits.getTotalHits());
        } catch (Exception e) {
            LOG.error(e);
        }
        return result;
    }
}
