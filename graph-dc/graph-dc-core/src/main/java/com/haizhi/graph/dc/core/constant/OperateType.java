package com.haizhi.graph.dc.core.constant;

/**
 * Created by chengangxiong on 2019/01/29
 */
public enum OperateType {

    INIT("初始化"), UPSERT("更新"), DELETE("删除")
    ;

    private String desc;
    OperateType(String desc){
        this.desc = desc;
    }
}
