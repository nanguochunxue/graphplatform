package com.haizhi.graph.dc.core.constant;

import com.haizhi.graph.common.constant.Status;

/**
 * Created by chengangxiong on 2019/01/08
 */
public enum  CoreStatus implements Status {

    GRAPH_PAGE_ERROR(2000, "资源库分页查询失败"),
    GRAPH_FIND_ERROR(2001, "资源库查询失败"),
    GRAPH_SAVE_ERROR(2002, "资源库保存失败"),
    GRAPH_DELETE_ERROR(2003, "资源库删除失败"),
    GRAPH_NOT_EXISTS(2003, "资源库不存在"),
    GRAPH_NAME_EXISTS(2004, "资源库名称已存在"),
    GRAPH_CANNOT_DELETE_FOR_EXISTS_SCHEMA(2005, "???资源库下存在表，无法删除、应该修改为判断表是否被关联，无法删除， TODO"),
    GRAPH_CANNOT_DELETE_FOR_EXISTS_TASK(2006, "该资源库下的表已被关联"),
    GRAPH_SCHEMA_IS_NULL(2007, "资源库名或表名为空"),
    GRAPH_IS_NULL(2008, "资源库名为空"),

    STORE_DELETE_ERROR(2020, "数据源删除失败"),
    STORE_SAVE_ERROR(2021, "数据源保存失败"),
    STORE_FIND_ERROR(2022, "数据源查询失败"),
    STORE_PAGE_ERROR(2023, "数据源分页查询失败"),
    STORE_NAME_EXISTS(2024, "数据源名称已存在"),

    SCHEMA_DELETE_ERROR(2040, "表数据删除失败"),
    SCHEMA_NOT_EXISTS(2041, "表数据不存在"),
    SCHEMA_SAVE_ERROR(2042, "表数据保存失败"),
    SCHEMA_FIND_ERROR(2043, "表数据查询失败"),
    SCHEMA_PAGE_ERROR(2044, "表数据分页查询失败"),
    SCHEMA_NAME_EXISTS(2045, "表数据名称已存在"),

    SCHEMA_FIELD_PAGE_ERROR(2060, "表字段分页查询失败"),
    SCHEMA_FIELD_FIND_ERROR(2061, "表字段查询失败"),
    SCHEMA_FIELD_SAVE_ERROR(2062, "表字段保存失败"),
    SCHEMA_FIELD_NAME_EXISTS(2063, "表字段名称已存在"),
    SCHEMA_FIELD_NOT_EXISTS(2064, "表字段不存在"),
    SCHEMA_FIELD_DELETE_ERROR(2065, "表字段删除失败"),
    SCHEMA_CANNOT_DELETE_FOR_EXISTS_TASK(2066,"此表已与数据接入任务关联，请先删除任务"),

    VERTEX_EDGE_PAGE_ERROR(2080, "顶点边分页查询失败"),
    VERTEX_EDGE_FIND_ERROR(2081, "顶点边查询失败"),
    VERTEX_EDGE_SAVE_ERROR(2082, "顶点边保存失败"),
    VERTEX_EDGE_DELETE_ERROR(2083, "顶点边删除失败"),
    VERTEX_EDGE_FIND_COLLECTIONS_ERROR(2084, "边关联的顶点表查询失败"),

    SCHEMA_IN_MAIN_FIELD_EXISTS(2090, "表{0}已经存在主字段；一个表只能有一个主字段"),
    SCHEMA_FIELD_CREATE_DEFAULT_ERROR(2091, "创建默认表字段失败"),
    DELETE_GRAPH_STORE_ERROR(2092, "删除关联信息失败【资源库-数据源】"),

    AUTHORIZED_SCHEMA_FIELD_PAGE_ERROR(2093, "获取授权的字段信息失败"),
    DISTINCT_FIELD_SCHEMA_FIELD_ERROR(2094, "模糊查询字段名失败");

    private int code;
    private String desc;

    CoreStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}