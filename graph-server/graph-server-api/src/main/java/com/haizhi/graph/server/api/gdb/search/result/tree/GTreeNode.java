package com.haizhi.graph.server.api.gdb.search.result.tree;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.server.api.gdb.search.result.GKeys;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/6/6.
 */
@Data
public class GTreeNode {
    private Map<String, Object> vertex;
    private List<Map<String, Object>> edges = new ArrayList<>();
    private List<Map<String, Object>> allEdges = new ArrayList<>();
    private List<GTreeNode> children = new ArrayList<>();

    public void addChild(GTreeNode node){
        if (node == null){
            return;
        }
        children.add(node);
    }

    public void addChildren(List<GTreeNode> nodes){
        for (GTreeNode node : nodes) {
            children.add(node);
        }
    }

    public void removeChild(GTreeNode node){
        if (node == null){
            return;
        }
        String vertexId = Getter.get(GKeys.ID, node.getVertex());
        for (Iterator<GTreeNode> iterator = children.iterator(); iterator.hasNext(); ) {
            GTreeNode child = iterator.next();
            String childVertexId = Getter.get(GKeys.ID, child.getVertex());
            if (vertexId.equals(childVertexId)){
                iterator.remove();
            }
        }
    }

    public GTreeNode getChild(GTreeNode node){
        String vertexId = Getter.get(GKeys.ID, node.getVertex());
        for (Iterator<GTreeNode> iterator = children.iterator(); iterator.hasNext(); ) {
            GTreeNode child = iterator.next();
            String childVertexId = Getter.get(GKeys.ID, child.getVertex());
            if (vertexId.equals(childVertexId)){
                return child;
            }
        }
        return null;
    }

    public void addEdge(Map<String, Object> edge){
        this.edges.add(edge);
    }

    public boolean existsEdge(String from, String to){
        for (Map<String, Object> edge : edges) {
            String eFrom = Getter.get(GKeys.FROM, edge);
            String eTo = Getter.get(GKeys.TO, edge);
            if (from.equals(eFrom) && to.equals(eTo)){
                return true;
            }
        }
        return false;
    }

    public void addEdges(List<Map<String, Object>> edges){
        this.edges.addAll(edges);
    }

    public void addAllEdges(List<Map<String, Object>> edges){
        for (Map<String, Object> edge : edges) {
            this.allEdges.add(Maps.newHashMap(edge));
        }
    }

    public GTreeNode clone(){
        return JSON.parseObject(JSON.toJSONString(this), GTreeNode.class);
    }
}
