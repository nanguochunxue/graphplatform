package com.haizhi.graph.dc.core.bean;

import com.haizhi.graph.common.constant.SchemaType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 * Created by chengmo on 2018/1/17.
 */
@Data
public class Schema implements Serializable {
    private long graphId;
    private String graphName;
    private long schemaId;
    private String schemaName;
    private String schemaNameCn;
    private SchemaType type;
    private int sequence;
    private boolean useSearch;
    private boolean useHBase;
    private boolean useGraphDb;
    private float searchWeight = 1.0F;

    // fields
    private String mainField;
    private Map<String, SchemaField> fieldMap = new LinkedHashMap<>();

    // relationships between vertex and edge
    private Set<String> vertexSchemas = new HashSet<>();
    private Set<String> edgeSchemas = new HashSet<>();
    private List<VertexEdge> vertexEdgeList = new ArrayList<>();

    public void addField(SchemaField field) {
        if (field == null) {
            return;
        }
        fieldMap.put(field.getField(), field);
    }

    public SchemaField getField(String field) {
        return this.fieldMap.get(field);
    }

    public boolean hasField(String field) {
        return this.fieldMap.containsKey(field);
    }

    @Data
    @AllArgsConstructor
    public static class VertexEdge {
        private String fromVertex;
        private String toVertex;
        private String edge;
    }
}
