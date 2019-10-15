package com.haizhi.graph.server.api.gdb.search.result.path;

import com.haizhi.graph.server.api.gdb.search.result.GEdge;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengmo on 2018/3/8.
 */
public class GPath {

    private List<GEdge> pairs = new ArrayList<>();
    private String firstVertex;
    private String lastVertex;

    public GPath joinPair(GEdge pair) {
        pairs.add(pair);
        if (firstVertex == null) {
            firstVertex = pair.fromVertex();
        }
        lastVertex = pair.toVertex();
        return this;
    }

    public GPath joinPairs(List<GEdge> pairs) {
        this.pairs.addAll(pairs);
        if (firstVertex == null) {
            firstVertex = pairs.get(0).fromVertex();
        }
        lastVertex = pairs.get(pairs.size() - 1).toVertex();
        return this;
    }

    public String id() {
        StringBuilder sb = new StringBuilder();
        for (GEdge pair : pairs) {
            sb.append(pair.getId() + "#");
        }
        return sb.toString();
    }

    public static String id(List<GEdge> pairs) {
        StringBuilder sb = new StringBuilder();
        for (GEdge pair : pairs) {
            sb.append(pair.getId() + "#");
        }
        return sb.toString();
    }

    public List<GEdge> pairs() {
        return pairs;
    }

    public String firstVertex() {
        return firstVertex;
    }

    public String lastVertex() {
        return lastVertex;
    }
}
