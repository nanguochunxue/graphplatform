package com.haizhi.graph.server.tiger.constant;

/**
 * Created by tanghaiyang on 2019/3/8.
 */
public enum TigerFieldType {
    STRING("string"),
    BOOL("bool"),
    LONG("uint"),
    INT("int"),
    FLOAT("float"),
    DOUBLE("double"),
    DATETIME("datetime"),

    STRING_SET("string_set"),
    STRING_LIST("string_list"),
    UINT_SET("uint_set"),
    INT_SET("int_set"),
    INT_LIST("int_list"),
    LIST("list"),
    SET("set"),
    MAP("map");

    private String label;

    TigerFieldType(String label) {
        this.label = label;
    }

    public String label() {
        return this.label;
    }


}
