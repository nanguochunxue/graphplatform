package com.haizhi.graph.common.constant;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chengmo on 2018/8/17.
 */
public enum FieldType {
    STRING("字符串"),
    LONG("整数"),
    DOUBLE("浮点数"),
    DATETIME("日期"),
    UNKNOWN("UNKNOWN");

    private static final Map<String, FieldType> codeLookup = new ConcurrentHashMap<>(6);

    static {
        for (FieldType type : EnumSet.allOf(FieldType.class)){
            codeLookup.put(type.name().toLowerCase(), type);
        }
    }

    private String label;

    FieldType(String label) {
        this.label = label;
    }

    public String label() {
        return this.label;
    }

    public boolean isNumber() {
        return equals(DOUBLE) || equals(LONG);
    }

    public boolean isText() {
        return equals(STRING) || equals(DATETIME);
    }

    public static FieldType fromCode(String code) {
        if (code == null){
            return FieldType.UNKNOWN;
        }
        FieldType data = codeLookup.get(code.toLowerCase());
        if (data == null) {
            return FieldType.UNKNOWN;
        }
        return data;
    }
}
