package com.haizhi.graph.server.es.index.mapping;

/**
 * Created by chengmo on 2017/12/28.
 */
public enum DataType {

    KEYWORD("keyword"),
    STRING("text"),
    LONG("long"),
    DOUBLE("double"),
    DATETIME("date"),
    OBJECT("object"),
    NONE("text");

    private String code;

    DataType(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static String getCode(String name){
        try {
            if (name == null){
                return DataType.NONE.code();
            }
            DataType dt = DataType.valueOf(name.toUpperCase());
            return dt.code();
        } catch (IllegalArgumentException e) {
            return DataType.NONE.code();
        }
    }
}
