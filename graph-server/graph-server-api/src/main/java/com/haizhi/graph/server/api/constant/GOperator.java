package com.haizhi.graph.server.api.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by tanghaiyang on 2019/5/5.
 */
public enum GOperator {
    EQ("EQ","等于"),
    NOT_EQ("NOT_EQ","不等于"),
    GT("GT","大于"),
    GTE("GTE","大于等于"),
    LT("LT","小于"),
    LTE("LTE","小于等于"),
    IN("IN","属于"),
    NOT_IN("NOT_IN","不属于"),
    IS_NULL("IS_NULL","是否为空"),
    IS_NOT_NULL("IS_NOT_NULL","是否不为空"),
    RANGE("RANGE","介于"),
    NOT_RANGE("NOT_RANGE","不介于"),
    MATCH("MATCH","匹配");

    private String value;
    private String desc;

    GOperator(String value, String desc){
        this.value = value;
        this.desc = desc;
    }

    public String getValue(){
        return this.value;
    }

    public String getDesc(){
        return this.desc;
    }

    public static GOperator byValue(String value){
        for(GOperator gOperator : values()){
            if(StringUtils.equalsIgnoreCase(value, gOperator.getValue())){
                return gOperator;
            }
        }
        return null;
    }
}
