package com.haizhi.graph.server.api.gdb.search.result;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by chengmo on 2018/6/6.
 */
public enum GResultType {
    DEFAULT,
    TREE,
    PATH;

    public static GResultType fromName(String name){
        if (StringUtils.isBlank(name)){
            return GResultType.DEFAULT;
        }
        try {
            return GResultType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return GResultType.DEFAULT;
        }
    }
}
