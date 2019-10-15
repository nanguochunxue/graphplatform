package com.haizhi.graph.search.api.gdb.model;

import com.haizhi.graph.common.model.BaseQo;
import com.haizhi.graph.search.api.gdb.constant.GdbQueryType;
import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * Created by chengmo on 2019/1/21.
 */
@Data
public class GdbQo extends BaseQo {
    // graph
    private String graph;
    private GdbQueryType type;
    private Set<String> startVertices;
    private Set<String> endVertices;
    private Set<String> vertexTables;
    private Set<String> edgeTables;
    private String direction = "ANY";
    private int maxDepth = 2;
    private int offset = -1;
    private int size = -1;
    private int maxSize = -1;

    // rule
    private Map<String, Object> rule;

    // filter
    private Map<String, Object> filter;
}
