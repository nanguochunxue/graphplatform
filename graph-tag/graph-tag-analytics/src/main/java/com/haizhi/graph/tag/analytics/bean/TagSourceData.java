package com.haizhi.graph.tag.analytics.bean;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by chengmo on 2018/3/1.
 */
public class TagSourceData {

    private String objectKey;
    private Map<String, TableRow> tableRowMap = new LinkedHashMap<>();

    public boolean hasTableRow(String tableName){
        return tableRowMap.containsKey(tableName);
    }

    public Set<String> getTableNames(){
        Set<String> result = new HashSet<>();
        for (String tableName : tableRowMap.keySet()) {
            if (tableName.contains(".")){
                tableName = StringUtils.substringBefore(tableName, ".");
            }
            result.add(tableName);
        }
        return result;
    }

    public TagSourceData addTableRow(String tableName, Map<String, Object> row){
        tableRowMap.put(tableName, new TableRow(tableName, row));
        return this;
    }

    public TableRow getTableRow(String tableName){
        return tableRowMap.get(tableName);
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public Map<String, TableRow> getTableRowMap() {
        return tableRowMap;
    }

    public void setTableRowMap(Map<String, TableRow> tableRowMap) {
        if (tableRowMap == null){
            return;
        }
        this.tableRowMap.clear();
        this.tableRowMap.putAll(tableRowMap);
    }
}
