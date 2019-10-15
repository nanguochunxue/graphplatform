package com.haizhi.graph.server.api.gdb.search.result;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by chengmo on 2018/6/7.
 */
@Data
public class GVertex {

    private String id;
    private Set<Integer> indexSet = new LinkedHashSet<>();

    public GVertex(String id) {
        this(id, null);
    }

    public GVertex(String id, Set<Integer> indexSet) {
        this.id = id;
        if (indexSet != null){
            this.indexSet.addAll(indexSet);
        }
    }

    public void addIndex(int index){
        this.indexSet.add(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GVertex gVertex = (GVertex) o;
        return id.equals(gVertex.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id + "_" + indexSet;
    }
}
