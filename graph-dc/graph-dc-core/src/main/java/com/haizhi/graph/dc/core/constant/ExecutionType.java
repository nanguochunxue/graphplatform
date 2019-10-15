package com.haizhi.graph.dc.core.constant;

/**
 * Created by chengangxiong on 2019/01/29
 */
public enum ExecutionType {
    ONCE("单次"), CRON("定时");
    private String desc;

    ExecutionType(String desc){
        this.desc = desc;
    }
}
