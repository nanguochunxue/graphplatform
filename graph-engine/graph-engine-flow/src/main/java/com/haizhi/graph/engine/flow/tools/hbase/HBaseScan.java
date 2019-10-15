package com.haizhi.graph.engine.flow.tools.hbase;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/3/26.
 */
public class HBaseScan {

    private String tableName;
    private String reduceKey;
    // key=field, value=fieldType
    private Map<String, String> fields = new LinkedHashMap<>();
    private String startRow;
    private String stopRow;

    public HBaseScan(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getReduceKey() {
        return reduceKey;
    }

    public void setReduceKey(String reduceKey) {
        this.reduceKey = reduceKey;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public String getStartRow() {
        return startRow;
    }

    public void setStartRow(String startRow) {
        this.startRow = startRow;
    }

    public String getStopRow() {
        return stopRow;
    }

    public void setStopRow(String stopRow) {
        this.stopRow = stopRow;
    }
}
