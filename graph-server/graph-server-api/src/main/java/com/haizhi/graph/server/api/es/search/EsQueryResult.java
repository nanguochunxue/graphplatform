package com.haizhi.graph.server.api.es.search;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2019/4/30.
 */
@Data
public class EsQueryResult {
    private long total;
    private List<Map<String, Object>> rows;
    private Map<String, Object> aggData;
}
