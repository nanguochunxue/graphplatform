package com.haizhi.graph.server.api.hbase.query.bean;


import com.haizhi.graph.server.api.hbase.admin.bean.ColumnType;
import lombok.Data;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/2/5.
 */
@Data
public class HBaseRangeQuery {

    private String database;
    private String table;
    private String url;

    /* range scan */
    private String startRow;
    private String stopRow;

    /* aggregations */
    private Map<String, Aggregation> aggregations = new LinkedHashMap<>();

    public HBaseRangeQuery(String database, String table) {
        this.database = database;
        this.table = table;
    }

    public String getTableName(){
        return database + ":" + table;
    }

    /**
     * Sets a range rowKey to the query.
     *
     * @param startRow
     * @param stopRow
     * @return
     */
    public HBaseRangeQuery setRange(String startRow, String stopRow) {
        this.startRow = startRow;
        this.stopRow = stopRow;
        return this;
    }

    /**
     * Added an aggregation to the query.
     *
     * @param key
     * @param column     family:column
     * @param columnType
     * @param stats
     * @return
     */
    public HBaseRangeQuery addAggregation(String key, String column, ColumnType columnType, Stats stats) {
        if (this.aggregations.size() >= 10) {
            throw new IllegalArgumentException("aggregations max size=10");
        }
        if (stats != Stats.COUNT && !ColumnType.aggColumnType(columnType)) {
            throw new IllegalArgumentException("column type can not be aggregated, " + columnType);
        }
        this.aggregations.put(key, new Aggregation(key, column, columnType, stats));
        return this;
    }

    /**
     * @param aggregations
     * @return
     */
    public HBaseRangeQuery addAggregations(Collection<Aggregation> aggregations){
        if (this.aggregations.size() >= 10) {
            throw new IllegalArgumentException("aggregations max size=10");
        }
        for (Aggregation aggregation : aggregations) {
            Stats stats = aggregation.getStats();
            ColumnType type = aggregation.getColumnType();
            if (stats != Stats.COUNT && !ColumnType.aggColumnType(type)){
                throw new IllegalArgumentException("column type can not be aggregated, " + type);
            }
            this.aggregations.put(aggregation.getKey(), aggregation);
        }
        return this;
    }

}
