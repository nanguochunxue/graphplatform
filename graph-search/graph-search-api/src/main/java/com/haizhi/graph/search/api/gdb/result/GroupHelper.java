package com.haizhi.graph.search.api.gdb.result;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.haizhi.graph.common.util.CodecUtils;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.search.api.gdb.model.GdbRule;
import com.haizhi.graph.search.api.model.qo.GdbAtlasQo;
import com.haizhi.graph.server.api.gdb.search.result.GKeys;
import com.haizhi.graph.server.api.gdb.search.result.tree.GTreeNode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/6/13.
 */
public class GroupHelper {

    /**
     * @param parent
     * @param current
     * @param searchQo
     */
    public static List<GTreeNode> groupByDirection(List<Map<String, Object>> edges,
                                        GTreeNode parent,
                                        GTreeNode current,
                                        GdbAtlasQo searchQo) {
        Map<String, Object> vertex = parent.getVertex();
        String vertexId = Getter.get(GKeys.ID, vertex);

        // 创建方向节点
        Map<String, GNodeGroup> groupMap = createAggNewVertices(searchQo, vertexId);
        Map<String, GNodeGroup> subGroupMap = new HashMap<>();
        for (Map<String, Object> edge : edges) {
            GEdgeGroup edgeGroup = new GEdgeGroup();
            edgeGroup.setEdgeTable(Getter.get(GKeys.SCHEMA, edge));
            edgeGroup.setVertexId(vertexId);
            edgeGroup.setFrom(Getter.get(GKeys.FROM, edge));
            edgeGroup.setTo(Getter.get(GKeys.TO, edge));

            // 添加边到分组节点
            putEdgeToGroup(edge, edgeGroup, subGroupMap);
        }

        // 添加分组节点到方向节点
        putSubGroupToGroup(subGroupMap, groupMap, current);
        return getResultNodes(groupMap);
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private static List<GTreeNode> getResultNodes(Map<String, GNodeGroup> groupMap){
        List<GTreeNode> nodes = new ArrayList<>();
        for (GNodeGroup group : groupMap.values()) {
            nodes.add(group.getFromNode());
            nodes.add(group.getToNode());
        }
        return nodes;
    }

    private static void putSubGroupToGroup(Map<String, GNodeGroup> subGroupMap, Map<String, GNodeGroup> groupMap,
                                           GTreeNode current) {
        Map<String, Object> vertex = current.getVertex();
        for (Map.Entry<String, GNodeGroup> entry : subGroupMap.entrySet()) {
            String schema = entry.getKey();
            GNodeGroup group = groupMap.get(schema);
            GNodeGroup subGroup = entry.getValue();

            // from
            GTreeNode subFrom = subGroup.getFromNode();
            if (subFrom != null) {
                GTreeNode from = group.getFromNode();
                subFrom.setVertex(Maps.newHashMap(vertex));
                String parentId = Getter.get(GKeys.ID, from.getVertex());
                for (Map<String, Object> edge : subFrom.getEdges()) {
                    edge.put(GKeys.TO, parentId);
                }
                from.addChild(subFrom);
            }

            // to
            GTreeNode subTo = subGroup.getToNode();
            if (subTo != null) {
                GTreeNode to = group.getToNode();
                subTo.setVertex(Maps.newHashMap(vertex));
                String parentId = Getter.get(GKeys.ID, to.getVertex());
                for (Map<String, Object> edge : subTo.getEdges()) {
                    edge.put(GKeys.FROM, parentId);
                }
                to.addChild(subTo);
            }
        }
    }

    private static void putEdgeToGroup(Map<String, Object> edge, GEdgeGroup edgeGroup, Map<String, GNodeGroup>
            groupMap) {
        if (edgeGroup.fromEdge()) {
            String fromSchema = StringUtils.substringBefore(edgeGroup.getFrom(), "/");
            GNodeGroup group = groupMap.get(fromSchema);
            if (group == null) {
                group = new GNodeGroup();
                group.setFromNode(new GTreeNode());
                groupMap.put(fromSchema, group);
            }
            GTreeNode from = group.getFromNode();
            if (from == null){
                from = new GTreeNode();
                group.setFromNode(from);
            }
            // edge
            Map<String, Object> newEdge = Maps.newHashMap(edge);
            newEdge.put(GKeys.TO, "");
            from.addEdge(newEdge);
        } else if (edgeGroup.toEdge()) {
            String toSchema = StringUtils.substringBefore(edgeGroup.getTo(), "/");
            GNodeGroup group = groupMap.get(toSchema);
            if (group == null) {
                group = new GNodeGroup();
                group.setToNode(new GTreeNode());
                groupMap.put(toSchema, group);
            }
            GTreeNode to = group.getToNode();
            if (to == null){
                to = new GTreeNode();
                group.setToNode(to);
            }
            // edge
            Map<String, Object> newEdge = Maps.newHashMap(edge);
            newEdge.put(GKeys.FROM, "");
            to.addEdge(newEdge);
        }
    }

    private static Map<String, GNodeGroup> createAggNewVertices(GdbAtlasQo searchQo, String parentId) {
        Map<String, GNodeGroup> schemaToGroup = new HashMap<>();
        Map<String, GdbRule.Vertex> newVertices = getAggNewVertices(searchQo);
        for (GdbRule.Vertex newVertex : newVertices.values()) {
            String schema = newVertex.getVertexTable();
            GNodeGroup group = new GNodeGroup();
            // newFromNode
            GTreeNode newFromNode = new GTreeNode();
            Map<String, Object> newFrom = new HashMap<>();
            String fromVertexLabel = newVertex.getFromVertexLabel();
            String fromVertexId = getNewId(schema, parentId, fromVertexLabel);
            newFrom.put(GKeys.ID, fromVertexId);
            newFrom.put(GKeys.SCHEMA, GdbRule.NEW_VERTEX_DIRECTION + "." + schema);
            newFrom.put(GKeys.LABEL, fromVertexLabel);
            newFromNode.setVertex(newFrom);
            group.setFromNode(newFromNode);
            // newFromEdge
            Map<String, Object> newFromEdge = new HashMap<>();
            newFromEdge.put(GKeys.FROM, fromVertexId);
            newFromEdge.put(GKeys.TO, parentId);
            newFromEdge.put(GKeys.TYPE, GdbRule.DIRECTION_EDGE);
            newFromEdge.put(GKeys.REMARK, fromVertexLabel);
            newFromEdge.put(GKeys.ID, getNewId(GdbRule.NEW_VERTEX_DIRECTION, fromVertexId, parentId));
            newFromNode.setEdges(Lists.newArrayList(newFromEdge));

            // newToNode
            GTreeNode newToNode = new GTreeNode();
            Map<String, Object> toFrom = new HashMap<>();
            String toVertexLabel = newVertex.getToVertexLabel();
            String toVertexId = getNewId(schema, parentId, toVertexLabel);
            toFrom.put(GKeys.ID, toVertexId);
            toFrom.put(GKeys.SCHEMA, GdbRule.NEW_VERTEX_DIRECTION + "." + schema);
            toFrom.put(GKeys.LABEL, toVertexLabel);
            newToNode.setVertex(toFrom);
            group.setToNode(newToNode);
            // newToEdge
            Map<String, Object> newToEdge = new HashMap<>();
            newToEdge.put(GKeys.FROM, parentId);
            newToEdge.put(GKeys.TO, toVertexId);
            newToEdge.put(GKeys.TYPE, GdbRule.DIRECTION_EDGE);
            newToEdge.put(GKeys.REMARK, toVertexLabel);
            newToEdge.put(GKeys.ID, getNewId(GdbRule.NEW_VERTEX_DIRECTION, parentId, toVertexId));
            newToNode.setEdges(Lists.newArrayList(newToEdge));

            schemaToGroup.put(schema, group);
        }
        return schemaToGroup;
    }

    private static String getNewId(String schema, String from, String to) {
        return schema + "/" + CodecUtils.md5(from + to);
    }

    private static Map<String, GdbRule.Vertex> getAggNewVertices(GdbAtlasQo searchQo) {
        String singleEdge = searchQo.getEdgeTables().iterator().next();
        GdbRule aggRule = searchQo.getRule().get(singleEdge);
        GdbRule.Aggregation agg = aggRule.getAggregation();
        return agg.getNewVertices();
    }
}
