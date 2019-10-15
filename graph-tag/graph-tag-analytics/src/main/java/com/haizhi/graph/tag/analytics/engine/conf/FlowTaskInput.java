package com.haizhi.graph.tag.analytics.engine.conf;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/4/3.
 */
public class FlowTaskInput implements Serializable {

    private String graph;
    private String sql;

    // key=SourceType + Schema
    private Map<String, FlowTaskSchema> schemas = new LinkedHashMap<>();

    public void addSchema(FlowTaskSchema sch) {
        if (sch != null) {
            schemas.put(sch.getSourceType() + "." + sch.getSchema(), sch);
        }
    }

    public void addSchemas(List<FlowTaskSchema> schemas){
        for (FlowTaskSchema schema : schemas) {
            addSchema(schema);
        }
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Map<String, FlowTaskSchema> getSchemas() {
        return schemas;
    }

    public void setSchemas(Map<String, FlowTaskSchema> schemas) {
        if (schemas == null){
            return;
        }
        this.schemas = schemas;
    }
}
