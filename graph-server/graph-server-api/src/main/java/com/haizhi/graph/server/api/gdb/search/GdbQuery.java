package com.haizhi.graph.server.api.gdb.search;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.server.api.gdb.search.aggregation.AggBuilder;
import com.haizhi.graph.server.api.gdb.search.bean.ParseFields;
import com.haizhi.graph.server.api.gdb.search.query.QBuilder;
import com.haizhi.graph.server.api.gdb.search.result.Projections;
import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chengmo on 2018/1/18.
 */
@Data
public class GdbQuery {

    private final String database;

    /* Graph SQL */
    private String graphSQL;
    private Map<String, Object> graphSQLParams;

    // findByIds
    private Map<String, Set<String>> schemas;

    // Query
    private QBuilder queryBuilder;

    // Aggregations
    private List<AggBuilder> aggBuilders = new ArrayList<>();

    // Result
    private Projections projections = new Projections();
    private Integer offset = 0;
    private Integer size = 10;
    private Integer timeout = 15;  // second

    /* log */
    private boolean debugEnabled;

    public GdbQuery(String database) {
        if (StringUtils.isBlank(database)) {
            throw new IllegalArgumentException("[database] must not be null");
        }
        this.database = database;
    }

    public String getDatabase() {
        return database;
    }

    public GdbQuery setGraphSQL(String graphSQL) {
        this.graphSQL = graphSQL;
        return this;
    }

    public GdbQuery setGraphSQL(String graphSQL, Map<String, Object> params) {
        this.graphSQL = graphSQL;
        this.graphSQLParams = params;
        return this;
    }

    public String getGraphSQL() {
        return graphSQL;
    }

    public Map<String, Object> getGraphSQLParams() {
        return graphSQLParams;
    }

    public GdbQuery setQuery(QBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
        return this;
    }

    public QBuilder getQuery() {
        return queryBuilder;
    }

    public GdbQuery addAggBuilder(AggBuilder aggBuilder) {
        this.aggBuilders.add(aggBuilder);
        return this;
    }

    public List<AggBuilder> getAggBuilders() {
        return aggBuilders;
    }

    public GdbQuery addProjection(String table, String field){
        this.projections.addProjection(table, field);
        return this;
    }

    public Projections getProjections() {
        return projections;
    }

    public Integer getOffset() {
        return offset;
    }

    public GdbQuery setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public Integer getSize() {
        return size;
    }

    public GdbQuery setSize(Integer size) {
        this.size = size;
        return this;
    }

    @Override
    public String toString(){
        JSONObject gdb = XContentBuilder.jsonObject();
        gdb.put(ParseFields.DATABASE_FIELD, database);
        gdb.put(ParseFields.GRAPH_SQL_FIELD, graphSQL);
        gdb.put(ParseFields.GRAPH_SQL_PARAMS_FIELD, graphSQLParams);

        // query
        if (queryBuilder != null){
            gdb.put(ParseFields.QUERY_FIELD, queryBuilder.toXContent().rootObject());
        }

        // aggregation
        JSONArray aggs = XContentBuilder.jsonArray();
        gdb.put(ParseFields.AGGREGATIONS_FIELD, aggs);
        for (AggBuilder aggBuilder : aggBuilders) {
            aggs.add(aggBuilder.toXContent().rootObject());
        }
        return JSON.toJSONString(gdb, true);
    }
}
