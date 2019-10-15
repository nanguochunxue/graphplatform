package com.haizhi.graph.dc.core.constant;

/**
 * Create by zhoumingbing on 2019-05-24
 */
public enum QueryType {
    ALL("全部搜索"),
    EN("英文关键字"),
    CH("中文关键字")
    ;

    private String desc;

    QueryType(String desc) {
        this.desc = desc;
    }
}
