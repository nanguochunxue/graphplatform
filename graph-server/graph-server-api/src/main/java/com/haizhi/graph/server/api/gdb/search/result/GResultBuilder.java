package com.haizhi.graph.server.api.gdb.search.result;

import com.google.common.collect.Maps;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.server.api.gdb.search.result.tree.GTreeNode;

import java.util.*;

/**
 * Created by chengmo on 2018/6/7.
 */
public class GResultBuilder {

    /**
     * @param root
     * @return
     */
    public static GResult getByTree(GTreeNode root) {
        if (root == null){
            return null;
        }
        List<Map<String, Object>> resultVertices = new ArrayList<>();
        List<Map<String, Object>> resultEdges = new ArrayList<>();

        // traverse
        traverseTree(root, resultVertices, resultEdges);

        GResult gResult = new GResult();
        gResult.setVertices(resultVertices);
        gResult.setEdges(resultEdges);
        return gResult;
    }

    /**
     * @param data
     * @return
     */
    public static GResult get(Object data) {
        List<Map<String, Object>> dataList = Getter.getListMap(data);
        List<Map<String, Object>> resultVertices = new ArrayList<>();
        List<Map<String, Object>> resultEdges = new ArrayList<>();

        // rebuild
        Set<String> set = new HashSet<>();
        for (Map<String, Object> map : dataList) {
            List<Map<String, Object>> vertices = Getter.getListMap(map.get(GKeys.VERTICES));
            for (Map<String, Object> vertex : vertices) {
                String id = Getter.get(GKeys.ID, vertex);
                if (set.add(id)) {
                    resultVertices.add(Maps.newHashMap(vertex));
                }
            }
            List<Map<String, Object>> edges = Getter.getListMap(map.get(GKeys.EDGES));
            for (Map<String, Object> edge : edges) {
                String id = Getter.get(GKeys.ID, edge);
                if (set.add(id)) {
                    resultEdges.add(Maps.newHashMap(edge));
                }
            }
        }
        GResult gResult = new GResult();
        gResult.setVertices(resultVertices);
        gResult.setEdges(resultEdges);
        return gResult;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private static void traverseTree(GTreeNode current, List<Map<String, Object>> vertices,
                                     List<Map<String, Object>> edges) {
        vertices.add(Maps.newHashMap(current.getVertex()));
        for (Map<String, Object> edge : current.getEdges()) {
            edges.add(Maps.newHashMap(edge));
        }
        // children
        for (GTreeNode subNode : current.getChildren()) {
            traverseTree(subNode, vertices, edges);
        }
    }
}
