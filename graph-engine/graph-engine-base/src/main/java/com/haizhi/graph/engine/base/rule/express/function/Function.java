package com.haizhi.graph.engine.base.rule.express.function;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by chengmo on 2018/6/8.
 */
public enum Function {

    MAX("max1"),
    MIN("min1"),
    SUM("sum"),
    PERCENT("percent"),
    UNKNOWN("unknown");

    private String code;

    Function(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static String getCode(String name){
        return fromName(name).code();
    }

    public static Function fromName(String name){
        if (StringUtils.isBlank(name)){
            return Function.UNKNOWN;
        }
        try {
            return Function.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Function.UNKNOWN;
        }
    }
}
