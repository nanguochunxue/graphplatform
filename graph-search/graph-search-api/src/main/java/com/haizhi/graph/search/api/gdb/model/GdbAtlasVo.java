package com.haizhi.graph.search.api.gdb.model;

import lombok.Data;

/**
 * Created by chengmo on 2018/6/5.
 */
@Data
public class GdbAtlasVo {
    private Object data;
    private Object treeData;

    public static GdbAtlasVo empty(){
        return new GdbAtlasVo();
    }
}
