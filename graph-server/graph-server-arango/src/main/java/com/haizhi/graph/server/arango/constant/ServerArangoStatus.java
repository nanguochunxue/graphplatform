package com.haizhi.graph.server.arango.constant;

import com.haizhi.graph.common.constant.Status;


public enum ServerArangoStatus implements Status {
    MISS_QUERY_PARAM(               6001, "图查询缺少必要参数"),
    GDB_EXPAND_QUERY_FAIL(          6002, "K层展开图查询失败:{0}"),
    GDB_SHORTEST_PATH_QUERY_FAIL(   6003, "最短路径图查询失败:{0}"),
    GDB_FULL_PATH_QUERY_FAIL(       6004, "全路径图查询失败:{0}"),
    GDB_COUNT_BYSCHEMA_FAIL(        6005,"表数据统计失败"),
    GDB_EXPAND_EXCAVATE_QUERY_FAIL( 6006,"挖掘边展开失败"),
    ARANGO_URL_INVALID_ERROR(       6007, "无效的Arango URL:{0}"),
    CREATE_ARANGO_CONNECTION_ERROR( 6008, "创建Arango连接失败:{0}"),
    AQL_EMPTY_ERROR(                6009, "AQL不能为空，Can't traverse with a empty GdbQuery or graph sql"),
    INVALID_PAGE_PARAM_ERROR(       6010, "illegal arguments. offset: {0}, size: {1}"),
    FIND_GRAPH_ERROR(               6011, "Arango图域名信息获取失败:{0}"),
    ARANGODB_QUERRY_ERROR(          6012, "Arango图查询失败:{0}"),
    GET_ARANGO_RESULT_FAILE(        6013, "error when getting future:{0}"),
    QUERY_ARANGO_FAILE(             6014, "query error with sql:{0}"),

    GDB_SEARCHATLAS_FAIL(           6015, "知识图谱查询失败:{0}"),
    GDB_SEARCHGDB_FAIL(             6016, "图查询失败:{0}"),
    GDB_SEARCHBYKEYS_FAIL(          6017, "主键搜索失败:{0}"),
    GDB_SEARCHNATIVE_FAIL(          6018, "原生搜索失败:{0}"),
    GDB_SEARCHNATIVE_FORBIDDEN(     6019, "原生关键字禁用"),
    ;

    private int code;
    private String desc;

    ServerArangoStatus(int code, String desc) {
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
