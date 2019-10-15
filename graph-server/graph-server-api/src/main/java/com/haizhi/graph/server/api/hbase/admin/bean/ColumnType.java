package com.haizhi.graph.server.api.hbase.admin.bean;

/**
 * Created by chengmo on 2018/5/9.
 */
public enum  ColumnType {
    STRING,
    LONG,
    DOUBLE,
    DATETIME;

    public static ColumnType fromName(String name){
        try {
            if (name == null){
                return ColumnType.STRING;
            }
            return ColumnType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ColumnType.STRING;
        }
    }

    public static boolean aggColumnType(ColumnType type){
        return ColumnType.LONG == type || ColumnType.DOUBLE == type;
    }
}
