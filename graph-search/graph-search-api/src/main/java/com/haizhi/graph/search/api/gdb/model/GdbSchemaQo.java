package com.haizhi.graph.search.api.gdb.model;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by tanghaiyang on 2019/1/21.
 */
@Data
public class GdbSchemaQo {

    private String graph;

    private Map<String, Object> schemas = new LinkedHashMap<>();

}
