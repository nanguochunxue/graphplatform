package com.haizhi.graph.tag.analytics.bean;

import java.util.Collections;
import java.util.Map;

/**
 * Created by chengmo on 2018/3/1.
 */
public class TableRow {

    private String tableName;
    private Map<String, Object> row;

    public TableRow() {
    }

    public TableRow(String tableName) {
        this.tableName = tableName;
    }

    public TableRow(String tableName, Map<String, Object> row) {
        this.tableName = tableName;
        this.row = row;
    }

    public Object getFieldValue(String field){
        if (row == null){
            return "";
        }
        return row.getOrDefault(field, "");
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, Object> getRow() {
        if (row == null){
            return Collections.emptyMap();
        }
        return row;
    }

    public void setRow(Map<String, Object> row) {
        this.row = row;
    }
}
