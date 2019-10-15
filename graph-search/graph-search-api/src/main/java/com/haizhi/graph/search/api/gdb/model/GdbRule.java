package com.haizhi.graph.search.api.gdb.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * Created by chengmo on 2018/6/8.
 */
@Data
public class GdbRule {
    public static final String EL_EDGE_SCRIPT = "@value(edgeScript)";
    public static final String NEW_VERTEX = "newVertex";
    public static final String NEW_VERTEX_DIRECTION = "newVertexDirection";
    public static final String AGG_EDGE = "AGG_EDGE";
    public static final String DIRECTION_EDGE = "DIRECTION_EDGE";
    private String schema;
    private String label;
    private String edgeScript;
    private String edgeOrder;
    private Aggregation aggregation;

    public boolean needAgg(){
        if (label == null || aggregation == null){
            return false;
        }
        if (!label.contains(EL_EDGE_SCRIPT)){
            return false;
        }
        if (StringUtils.isBlank(edgeScript)){
            return false;
        }
        if (StringUtils.isBlank(aggregation.getType())){
            return false;
        }
        return true;
    }

    public boolean needAggNewVertices(){
        if (!needAgg()){
            return false;
        }
        if (CollectionUtils.isEmpty(aggregation.getNewVertices())){
            return false;
        }
        return true;
    }

    @Data
    public static class Aggregation {
        private String type = "direction";
        private String field;
        private Map<String, Vertex> newVertices;
    }

    @Data
    public static class Vertex{
        private String vertexTable;
        private String fromVertexLabel;
        private String toVertexLabel;
    }
}
