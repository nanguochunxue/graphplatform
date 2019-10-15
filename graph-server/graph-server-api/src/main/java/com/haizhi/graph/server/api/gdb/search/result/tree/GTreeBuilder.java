package com.haizhi.graph.server.api.gdb.search.result.tree;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.server.api.gdb.search.result.GEdge;
import com.haizhi.graph.server.api.gdb.search.result.GKeys;
import com.haizhi.graph.server.api.gdb.search.result.GVertex;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by chengmo on 2018/6/7.
 */
public class GTreeBuilder {
    private static final GLog LOG = LogFactory.getLogger(GTreeBuilder.class);

    /**
     * Get graph tree node by a single vertex.
     *
     * @param rootVertexId
     * @param data
     * @return
     */
    public static GTreeNode getBySingleVertex(String rootVertexId, Object data) {
        List<Map<String, Object>> dataList = Getter.getListMap(data);
        if (dataList == null || dataList.isEmpty()) {
            return null;
        }
        GVertex rootVertex = new GVertex(rootVertexId);
        Set<GEdge> allEdges = buildAllEdges(rootVertex, dataList);
        LOG.info("all edges:\n{0}", JSON.toJSONString(allEdges, true));
        return buildTree(rootVertex, allEdges, dataList);
    }

    /**
     * Use for debug.
     *
     * @param rootVertexId
     * @param data
     * @param filterVertexIds
     * @return
     */
    public static GTreeNode getBySingleVertex(String rootVertexId, Object data, String... filterVertexIds){
        List<Map<String, Object>> dataList = Getter.getListMap(data);
        if (dataList == null || dataList.isEmpty()) {
            return null;
        }
        GVertex rootVertex = new GVertex(rootVertexId);
        Set<GEdge> allEdges = buildAllEdges(rootVertex, dataList);
        LOG.info("all edges:\n{0}", JSON.toJSONString(allEdges, true));
        Set<GEdge> filterEdges = new LinkedHashSet<>();
        for (GEdge edge : allEdges) {
            for (String filterVertexId : filterVertexIds) {
                if (edge.contains(filterVertexId)){
                    filterEdges.add(edge);
                }
            }
        }
        LOG.info("all edges:\n{0}", JSON.toJSONString(filterEdges, true));
        return buildTree(rootVertex, filterEdges, dataList);
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private static Set<GEdge> buildAllEdges(GVertex rootVertex, List<Map<String, Object>> dataList){
        String rootVertexId = rootVertex.getId();
        Map<String, GEdge> edgeMap = new LinkedHashMap<>();
        boolean flag = false;
        for (int i = 0; i < dataList.size(); i++) {
            List<Map<String, Object>> edges = Getter.getListMap(dataList.get(i).get(GKeys.EDGES));
            for (int j = 0; j < edges.size(); j++) {
                Map<String, Object> edge = edges.get(j);
                String fromKey = Getter.get(GKeys.FROM, edge);
                String toKey = Getter.get(GKeys.TO, edge);
                if (!flag && (rootVertexId.equals(fromKey) || rootVertexId.equals(toKey))) {
                    rootVertex.addIndex(i);
                    flag = true;
                }
                GEdge gEdge = new GEdge(fromKey, toKey);
                String id = gEdge.getId();
                GEdge tempEdge = edgeMap.get(id);
                if (tempEdge == null) {
                    tempEdge = gEdge;
                    tempEdge.addIndex(i);
                    edgeMap.put(id, tempEdge);
                    continue;
                }
                tempEdge.addIndex(i);
            }
        }
        return Sets.newLinkedHashSet(edgeMap.values());
    }

    private static GTreeNode buildTree(GVertex rootVertex, Set<GEdge> allEdges, List<Map<String, Object>> dataList){
        MutableGraph<GVertex> graph = GraphBuilder.directed().build();
        traversePutEdge(allEdges, null, rootVertex, graph, dataList);
        Iterable<GVertex> topology = Traverser.forGraph(graph)
                .depthFirstPostOrder(rootVertex);
        List<GVertex> list = Lists.newArrayList(topology.iterator());
        Collections.reverse(list);
        System.out.println(list);

        // build tree
        GTreeNode root = new GTreeNode();
        Map<String, Object> vertex = getVertex(dataList, rootVertex);
        root.setVertex(Maps.newHashMap(vertex));
        doBuildTree(dataList, graph, root, rootVertex);
        return root;
    }

    private static void doBuildTree(List<Map<String, Object>> dataList, MutableGraph<GVertex> graph,
                                    GTreeNode gNode, GVertex gVertex) {
        Set<GVertex> successors = graph.successors(gVertex);
        List<GTreeNode> children = new ArrayList<>();
        for (GVertex successor : successors) {
            GTreeNode sucNode = new GTreeNode();
            Map<String, Object> sucVertex = getVertex(dataList, successor);
            if (sucVertex == null) {
                continue;
            }
            sucNode.setVertex(Maps.newHashMap(sucVertex));
            sucNode.addEdges(getEdges(dataList, gVertex, successor));
            doBuildTree(dataList, graph, sucNode, successor);
            children.add(sucNode);
        }
        gNode.setChildren(children);
    }

    private static void traversePutEdge(Set<GEdge> allEdges, GVertex parentVertex, GVertex currentVertex,
                                        MutableGraph<GVertex> graph, List<Map<String, Object>> dataList) {
        Set<GEdge> subEdges = Sets.filter(allEdges, key -> {
            if (parentVertex != null && key.contains(parentVertex.getId())) {
                return false;
            }
            return key.contains(currentVertex.getId());
        });
        for (GEdge edge : subEdges) {
            String subVertexId = edge.getAnotherVertex(currentVertex.getId());
            GVertex subVertex = new GVertex(subVertexId, edge.indexSet());
            if (!graph.nodes().isEmpty()){
                Set<GVertex> successors = graph.successors(currentVertex);
                for (GVertex successor : successors){
                    if (successor.getId().equals(subVertex.getId())){
                        successor.getIndexSet().addAll(subVertex.getIndexSet());
                    }
                }
            }
            graph.putEdge(currentVertex, subVertex);
            if (Graphs.hasCycle(graph)) {
                /*LOG.warn("has cycle: {0}={1}, edges=\n{2}", currentVertex, subVertexId,
                        JSON.toJSONString(dataList.get(edge.index())));*/
                graph.removeEdge(currentVertex, subVertex);
                continue;
            }
            traversePutEdge(allEdges, currentVertex, subVertex, graph, dataList);
        }
    }

    private static Map<String, Object> getVertex(List<Map<String, Object>> dataList, GVertex gVertex) {
        for (Integer index : gVertex.getIndexSet()) {
            Map<String, Object> map = dataList.get(index);
            List<Map<String, Object>> vertices = Getter.getListMap(map.get(GKeys.VERTICES));
            String vertexId = gVertex.getId();
            for (Map<String, Object> vertex : vertices) {
                String id = Getter.get(GKeys.ID, vertex);
                if (id.equals(vertexId)) {
                    String schema = StringUtils.substringBefore(id, "/");
                    vertex.put(GKeys.SCHEMA, schema);
                    return vertex;
                }
            }
        }
        return null;
    }

    private static List<Map<String, Object>> getEdges(List<Map<String, Object>> dataList,
                                                      GVertex parent,
                                                      GVertex current) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        String parentVertexId = parent.getId();
        String currentVertexId = current.getId();
        Set<String> set = new HashSet<>();
        for (Integer index : current.getIndexSet()) {
            Map<String, Object> map = dataList.get(index);
            List<Map<String, Object>> edges = Getter.getListMap(map.get(GKeys.EDGES));
            for (Map<String, Object> edge : edges) {
                String id = Getter.get(GKeys.ID, edge);
                if (!set.add(id)) {
                    continue;
                }
                String from = Getter.get(GKeys.FROM, edge);
                String to = Getter.get(GKeys.TO, edge);
                if ((from.equals(parentVertexId) && to.equals(currentVertexId)) ||
                        (from.equals(currentVertexId) && to.equals(parentVertexId))){
                    String schema = StringUtils.substringBefore(id, "/");
                    edge.put(GKeys.SCHEMA, schema);
                    resultList.add(Maps.newHashMap(edge));
                }
            }
        }
        return resultList;
    }
}
