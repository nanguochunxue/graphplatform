package com.haizhi.graph.search.api.gdb.result;

import lombok.Data;

/**
 * Created by chengmo on 2018/6/12.
 */
@Data
public class GEdgeGroup {
    private String edgeTable;
    private String vertexId;
    private String from;
    private String to;

    public boolean fromEdge(){
        return to != null && to.equals(vertexId);
    }

    public boolean toEdge(){
        return from != null && from.equals(vertexId);
    }

    @Override
    public String toString() {
        return "GEdgeGroup{" +
                "edgeTable='" + edgeTable + '\'' +
                ", vertexId='" + vertexId + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
