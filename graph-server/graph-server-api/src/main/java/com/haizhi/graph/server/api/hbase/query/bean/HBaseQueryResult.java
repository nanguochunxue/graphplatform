package com.haizhi.graph.server.api.hbase.query.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/2/5.
 */
public class HBaseQueryResult {

    private Map<String, TableResult> results = new LinkedHashMap<>();

    @JSONField(serialize = false)
    public TableResult getSingleResult() {
        if (results.isEmpty()){
            return new TableResult();
        }
        return results.values().iterator().next();
    }

    public Map<String, TableResult> getResults() {
        return results;
    }

    public void setResults(Map<String, TableResult> results) {
        this.results = results;
    }
}
