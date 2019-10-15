package com.haizhi.graph.server.api.gdb.search;

import com.alibaba.fastjson.annotation.JSONField;
import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.model.BaseQo;
import com.haizhi.graph.server.api.gdb.search.bean.GDirection;
import com.haizhi.graph.server.api.gdb.search.bean.GQueryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;
import java.util.Set;

/**
 * Created by chengmo on 2019/3/13.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GQuery extends BaseQo {
    private String graph;
    private GQueryType type;
    private Set<Vertex> startVertices;
    private Set<Vertex> endVertices;
    private Set<String> vertexTables;
    private Set<String> edgeTables;
    private GDirection direction = GDirection.ANY;
    private int maxDepth = 2;
    private int offset = -1;
    private int size = -1;
    private int maxSize = -1;
    private String filterExpression;

    // filter
    @JSONField(serialize=false)
    private Map<String, Object> filter;

    // key=${schema}.${field} tv_user.name
    @JSONField(serialize=false)
    private Map<String, FieldType> fields;

    @Data
    public static class Vertex{
        private String schema;
        private String objectKey;
    }
}
