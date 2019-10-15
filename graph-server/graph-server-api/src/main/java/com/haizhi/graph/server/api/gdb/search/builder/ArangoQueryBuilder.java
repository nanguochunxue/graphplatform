package com.haizhi.graph.server.api.gdb.search.builder;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by tanghaiyang on 2019/4/8.
 */
@Data
public class ArangoQueryBuilder implements QueryBuilder {

    private Set<String> vertexTables = new LinkedHashSet<>();
    private Set<String> edgeTables = new LinkedHashSet<>();
    private Set<String> startVertices = new LinkedHashSet<>();
    private Set<String> endVertices = new LinkedHashSet<>();
    private Integer maxDepth = 2;
    private String direction = "ANY";
    private Integer offset = 0;
    private Integer size = 10;

}
