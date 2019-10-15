package com.haizhi.graph.server.api.constant;

import com.haizhi.graph.common.constant.Status;


public enum ServerEsStatus implements Status {

    SEARCH_FAIL(           7001, "通用搜索异常:{0}"),
    SEARCH_BY_KEYS_FAIL(   7002, "主键搜索异常:{0}"),
    SEARCH_NATIVE_FAIL(    7003, "原生搜索异常:{0}"),
    EXECUTE_PROXY_FAIL(    7004, "原生搜索异常:{0}"),

    SERVER_QUERY_FAIL(     7011, "通用搜索条件错误:{0}"),
    SERVER_IDS_QUERY_FAIL( 7012, "主键搜索条件错误:{0}"),
    SERVER_DSL_QUERY_FAIL( 7013, "原生搜索条件错误:{0}"),
    SERVER_RESULT_FAIL(    7014, "通用搜索条件错误:{0}"),
    SERVER_IDS_RESULT_FAIL(7015, "主键搜索结果错误:{0}"),
    SERVER_DSL_RESULT_FAIL(7016, "原生搜索结果错误:{0}"),
    SEARCH_ERROR(          7017, "通用搜索条件错误:{0}"),
    SEARCHBYIDS_ERROR(     7018, "主键搜索条件错误:{0}"),
    SEARCH_SORT_ERROR(     7019, "搜索条件排序字段错误:{0}"),
    ;

    private int code;
    private String desc;

    ServerEsStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}
