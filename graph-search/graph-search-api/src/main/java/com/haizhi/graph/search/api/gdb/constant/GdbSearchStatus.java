package com.haizhi.graph.search.api.gdb.constant;

import com.haizhi.graph.common.constant.Status;


public enum GdbSearchStatus implements Status {

    GDP_FILTER_ERROR(               3001, "过滤树解析失败，请确认过滤条件参数是否符合标准"),
    GDP_FIELD_PARSE_ERROR(          3002, "过滤树解析失败"),
    GDB_EXPAND_QUERY_FAIL(          3003, "K层展开图查询失败{0}"),
    GDB_SHORTEST_PATH_QUERY_FAIL(   3004, "最短路径图查询失败{0}"),
    GDB_FULL_PATH_QUERY_FAIL(       3004, "全路径图查询失败{0}"),
    GDB_COUNT_BYSCHEMA_FAIL(        3005, "表数据统计失败"),
    GDB_EXPAND_EXCAVATE_QUERY_FAIL( 3006, "挖掘边展开失败"),

    GQL_EMPTY_ERROR(                3007, "GQL不能为空"),
    GQL_ERROR(                      3008, "GQL语法错误"),;

    private int code;
    private String desc;

    GdbSearchStatus(int code, String desc) {
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
