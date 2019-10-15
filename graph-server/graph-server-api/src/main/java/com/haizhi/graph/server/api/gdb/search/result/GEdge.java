package com.haizhi.graph.server.api.gdb.search.result;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by chengmo on 2018/3/8.
 */
public class GEdge {

    private String fromVertex;
    private String toVertex;
    private int index;
    private Set<Integer> indexSet = new LinkedHashSet<>();

    public GEdge(String fromVertex, String toVertex) {
        this.fromVertex = fromVertex;
        this.toVertex = toVertex;
    }

    public GEdge(String fromVertex, String toVertex, int index) {
        this.fromVertex = fromVertex;
        this.toVertex = toVertex;
        this.index = index;
    }

    public boolean contains(String vertex){
        if(fromVertex.equals(vertex)){
            return true;
        }
        if(toVertex.equals(vertex)){
            return true;
        }
        return false;
    }

    public String getAnotherVertex(String vertex){
        if(fromVertex.equals(vertex)){
            return toVertex;
        }
        if(toVertex.equals(vertex)){
            return fromVertex;
        }
        return "";
    }

    public String fromVertex() {
        return fromVertex;
    }

    public String toVertex() {
        return toVertex;
    }

    public int index() {
        return index;
    }

    public Set<Integer> indexSet(){
        return indexSet;
    }

    public void addIndex(int index){
        this.indexSet.add(index);
    }

    public String getId() {
        return fromVertex + "=" + toVertex;
    }

    public GEdge clone() {
        return new GEdge(fromVertex, toVertex, index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GEdge gEdge = (GEdge) o;

        if (fromVertex != null ? !fromVertex.equals(gEdge.fromVertex) : gEdge.fromVertex != null) return false;
        return toVertex != null ? toVertex.equals(gEdge.toVertex) : gEdge.toVertex == null;
    }

    @Override
    public int hashCode() {
        int result = fromVertex != null ? fromVertex.hashCode() : 0;
        result = 31 * result + (toVertex != null ? toVertex.hashCode() : 0);
        return result;
    }
}
