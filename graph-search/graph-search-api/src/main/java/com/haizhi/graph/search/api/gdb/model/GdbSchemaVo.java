package com.haizhi.graph.search.api.gdb.model;

import lombok.Data;

import java.util.Map;

/**
 * Created by tanghaiyang on 2019/1/21.
 */
@Data
public class GdbSchemaVo {

    private Map<String, Object> data;

    public static GdbSchemaVo empty(){
        return new GdbSchemaVo();
    }

}
