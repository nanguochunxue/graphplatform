package com.haizhi.graph.server.api.hbase.admin.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/3/23.
 */
@Data
public class HBaseRows {

    public static final String DEFAULT_FAMILY = "objects";
    private String family;
    private Map<String, ColumnType> columnTypes = new HashMap<>();
    private List<Map<String, Object>> rows = new ArrayList<>();

    public HBaseRows() {
        this(DEFAULT_FAMILY);
    }

    public HBaseRows(String family) {
        this.family = family;
    }

    public HBaseRows addColumnType(String column, ColumnType type){
        this.columnTypes.put(column, type);
        return this;
    }

    public HBaseRows addRow(Map<String, Object> row){
        this.rows.add(row);
        return this;
    }

    public HBaseRows addRows(List<Map<String, Object>> rows){
        this.rows.addAll(rows);
        return this;
    }

    public HBaseRows addStringRow(Map<String, String> row){
        this.rows.add(new HashMap<>(row));
        return this;
    }

    public HBaseRows addStringRows(List<Map<String, String>> rows){
        if (rows != null){
            for (Map<String, String> row : rows) {
                this.rows.add(new HashMap<>(row));
            }
        }
        return this;
    }

    public void clear(){
        this.rows.clear();
    }

}
