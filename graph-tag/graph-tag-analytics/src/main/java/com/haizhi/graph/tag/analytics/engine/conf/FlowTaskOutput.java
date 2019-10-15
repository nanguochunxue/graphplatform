package com.haizhi.graph.tag.analytics.engine.conf;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/4/3.
 */
public class FlowTaskOutput implements Serializable {

    private String graph;

    // key=SourceType + Schema
    private Map<String, FlowTaskSchema> schemas = new LinkedHashMap<>();

    public void addSchema(FlowTaskSchema sch) {
        if (sch != null) {
            schemas.put(sch.getSourceType() + "." + sch.getSchema(), sch);
        }
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
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
