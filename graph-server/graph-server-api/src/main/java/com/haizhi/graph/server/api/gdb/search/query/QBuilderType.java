package com.haizhi.graph.server.api.gdb.search.query;

/**
 * Created by chengmo on 2018/1/23.
 */
public enum QBuilderType {
    GRAPH,
    BOOL,
    VERTEX,
    EDGE,
    TERM,
    TERMS,
    LIKE,
    IS_NULL,
    RANGE,
    NONE;

    public static QBuilderType fromName(String name) {
        try {
            if (name == null || name.isEmpty()) {
                return QBuilderType.NONE;
            }
            return QBuilderType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return QBuilderType.NONE;
        }
    }
}
