package com.haizhi.graph.search.api.gdb.result;

import com.google.common.collect.Maps;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.CodecUtils;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.engine.base.rule.Rule;
import com.haizhi.graph.engine.base.rule.express.RuleExpressRunner;
import com.haizhi.graph.search.api.gdb.model.GdbRule;
import com.haizhi.graph.search.api.model.qo.GdbAtlasQo;
import com.haizhi.graph.server.api.gdb.search.result.GKeys;
import com.haizhi.graph.server.api.gdb.search.result.tree.GTreeNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chengmo on 2018/6/8.
 */
public class GdbResultBuilder {
    private static final GLog LOG = LogFactory.getLogger(GdbResultBuilder.class);
    private static final Pattern EL_ELEMENT_PATTERN = Pattern.compile("@((field)|(value))\\([A-Za-z0-9_.$]+\\)");
    private static RuleExpressRunner runner = new RuleExpressRunner();
    private static final String COMPANY = "Company";

    /**
     * @param searchQo
     * @param root
     * @return
     */
    public static GTreeNode rebuild(GdbAtlasQo searchQo, GTreeNode root) {
        if (root == null) {
            return null;
        }
        rebuildTree(null, root, searchQo);
        return root;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private static void rebuildTree(GTreeNode parent, GTreeNode current, GdbAtlasQo searchQo) {
        String schema = Getter.get(GKeys.SCHEMA, current.getVertex());
        boolean isNewDirectionNode = schema.startsWith(GdbRule.NEW_VERTEX_DIRECTION);

        // 如果是方向节点直接去下一层
        if (isNewDirectionNode) {
            GTreeNode cloneNode = current.clone();
            for (GTreeNode subNode : cloneNode.getChildren()) {
                rebuildTree(current, subNode, searchQo);
            }
            return;
        }

        // 构建当前节点的顶点信息
        doRebuildVertex(parent, current, searchQo);

        // 构建当前节点(包括聚合)
        doRebuildCurrentNode(parent, current, searchQo);

        // 如果父节点不为空，取父节点的当前子节点替换当前节点，因为数据更新都保存在parent的当前子节点中
        // 注意：parent.current != current
        if (parent != null) {
            GTreeNode child = parent.getChild(current);
            current = child != null ? child.clone() : current;
        }

        // 递归处理下一层
        GTreeNode cloneNode = current.clone();
        for (GTreeNode subNode : cloneNode.getChildren()) {
            rebuildTree(current, subNode, searchQo);
        }

        // 更新当前节点到父节点
        if (parent != null && !searchQo.needAggNewVertices()) {
            parent.removeChild(current);
            parent.addChild(current);
        }
    }

    private static void doRebuildCurrentNode(GTreeNode parent, GTreeNode current, GdbAtlasQo searchQo) {
        if (CollectionUtils.isEmpty(current.getEdges())) {
            return;
        }
        // 是否为方向分组的新建节点
        boolean isNewNode = false;
        if (parent != null) {
            String vertexType = Getter.get(GKeys.TYPE, parent.getVertex());
            isNewNode = vertexType.equals(GdbRule.NEW_VERTEX);
        }
        List<Map<String, Object>> needEdges = new ArrayList<>();
        List<Map<String, Object>> needAggEdges = new ArrayList<>();
        List<Map<String, Object>> needAggNewEdges = new ArrayList<>();
        Map<String, GdbRule> rule = searchQo.getRule();
        for (Map<String, Object> edge : current.getEdges()) {
            String edgeSchema = Getter.get(GKeys.SCHEMA, edge);

            // 构建普通边Label
            if (!searchQo.needAgg(edgeSchema)) {
                parseAndMakeEdgeLabel(rule, edge, edgeSchema);
                needEdges.add(Maps.newHashMap(edge));
                continue;
            }
            // 是否需要按照边的方向分组集合，并创建新方向顶点
            if (!isNewNode && searchQo.needAggNewVertices(edgeSchema)) {
                needAggNewEdges.add(edge);
                continue;
            }
            // 仅仅需要聚合
            needAggEdges.add(edge);
        }

        // 按边的方向分组聚合
        doRebuildNeedAggNewEdges(needAggNewEdges, parent, current, searchQo);

        // 仅仅需要聚合
        doRebuildNeedAggEdges(needAggEdges, parent, current, searchQo);

        // 如果是按边的方向分组聚合，直接返回
        if (!CollectionUtils.isEmpty(needAggNewEdges)) {
            return;
        }

        // 添加搜索原始边数据到父节点（冗余处理，便于前端列表展示）
        parent.addAllEdges(current.getEdges());
        // 添加普通边到父节点
        parent.addEdges(needEdges);
        // 清空父节点的当前子节点边原始数据（因为递归中current节点为克隆对象，便于动态扩展节点）
        GTreeNode child = parent.getChild(current);
        if (child != null) {
            child.setEdges(Collections.emptyList());
        }
    }

    private static void doRebuildNeedAggNewEdges(List<Map<String, Object>> needAggNewEdges,
                                                 GTreeNode parent,
                                                 GTreeNode current,
                                                 GdbAtlasQo searchQo) {
        if (CollectionUtils.isEmpty(needAggNewEdges)) {
            return;
        }
        List<GTreeNode> newNodes = GroupHelper.groupByDirection(needAggNewEdges, parent, current, searchQo);
        parent.removeChild(current);
        String parentVertexId = Getter.get(GKeys.ID, parent.getVertex());
        for (GTreeNode newNode : newNodes) {
            newNode.getVertex().put(GKeys.TYPE, GdbRule.NEW_VERTEX);
            GTreeNode cloneNode = newNode.clone();

            // 递归聚合
            rebuildTree(null, cloneNode, searchQo);

            // 建立与父节点的关联
            GTreeNode directionNode = cloneNode.clone();
            List<Map<String, Object>> edges = directionNode.getEdges();
            for (Iterator<Map<String, Object>> iterator = edges.iterator(); iterator.hasNext()
                    ; ) {
                Map<String, Object> edge = iterator.next();
                String from = Getter.get(GKeys.FROM, edge);
                String to = Getter.get(GKeys.TO, edge);
                if (parentVertexId.equals(from) || parentVertexId.equals(to)) {
                    if (!parent.existsEdge(from, to)) {
                        parent.addEdge(Maps.newHashMap(edge));
                    }
                    iterator.remove();
                }
            }
            // 更数据到父节点的当前子节点
            GTreeNode curChild = parent.getChild(directionNode);
            if (curChild == null) {
                curChild = directionNode;
                parent.addChild(curChild);
                continue;
            }
            curChild.addChildren(directionNode.getChildren());
            curChild.addEdges(directionNode.getEdges());
            curChild.addAllEdges(directionNode.getAllEdges());
        }
    }

    private static void doRebuildNeedAggEdges(List<Map<String, Object>> needAggEdges,
                                              GTreeNode parent,
                                              GTreeNode current,
                                              GdbAtlasQo searchQo) {
        if (CollectionUtils.isEmpty(needAggEdges)) {
            return;
        }
        /*String _key = Getter.get("_key", current.getVertex());
        if (_key.equals("L3KFL54944A834757515A1D521C6854F")){
            JSONUtils.println(current);
        }*/
        Map<GEdgeGroup, Object> edgeGroupMap = groupByEdge(needAggEdges, parent);
        List<Map<String, Object>> aggEdges = edgeAggregate(edgeGroupMap, parent, current, searchQo);
        if (parent == null) {
            return;
        }

        // 如果有统计边，覆盖到父节点(先把自己从父节点移除)，同时清空当前节点的边数据
        if (!aggEdges.isEmpty()) {
            GTreeNode aggNode = current.clone();
            aggNode.setEdges(Collections.emptyList());
            parent.removeChild(aggNode);
            parent.addChild(aggNode);
            parent.addEdges(aggEdges);
        }
    }

    private static Map<GEdgeGroup, Object> groupByEdge(List<Map<String, Object>> edges, GTreeNode parent) {
        if (CollectionUtils.isEmpty(edges)) {
            return Collections.emptyMap();
        }
        Map<String, Object> vertex = parent.getVertex();
        String vertexId = Getter.get(GKeys.ID, vertex);
        Map<GEdgeGroup, Object> edgeGroupMap = new HashMap<>();
        for (Map<String, Object> edge : edges) {
            GEdgeGroup gEdge = new GEdgeGroup();
            gEdge.setEdgeTable(Getter.get(GKeys.SCHEMA, edge));
            gEdge.setVertexId(vertexId);
            gEdge.setFrom(Getter.get(GKeys.FROM, edge));
            gEdge.setTo(Getter.get(GKeys.TO, edge));
            List<Map<String, Object>> groupEdges = Getter.getListMap(edgeGroupMap.get(gEdge));
            if (groupEdges == null) {
                groupEdges = new ArrayList<>();
                edgeGroupMap.put(gEdge, groupEdges);
            }
            groupEdges.add(Maps.newHashMap(edge));
            //allEdges.add(Maps.newHashMap(edge));
        }
        return edgeGroupMap;
    }

    private static List<Map<String, Object>> edgeAggregate(Map<GEdgeGroup, Object> edgeGroupMap,
                                                           GTreeNode parent,
                                                           GTreeNode current,
                                                           GdbAtlasQo searchQo) {
        if (CollectionUtils.isEmpty(edgeGroupMap)) {
            return Collections.emptyList();
        }
        Map<String, GdbRule> rule = searchQo.getRule();
        Map<String, Object> parentVertex = parent.getVertex();
        List<Map<String, Object>> aggEdges = new ArrayList<>();
        for (Map.Entry<GEdgeGroup, Object> entry : edgeGroupMap.entrySet()) {
            GEdgeGroup group = entry.getKey();
            String schema = group.getEdgeTable();
            if (StringUtils.isBlank(schema)) {
                continue;
            }
            GdbRule edgeRule = rule.get(schema);
            if (edgeRule == null) {
                throw new IllegalArgumentException("edge rule missing with " + schema);
            }

            // edgeLabel and edgeScript
            String edgeLabel = edgeRule.getLabel();
            String edgeScript = edgeRule.getEdgeScript();
            List<Map<String, Object>> groupEdges = Getter.getListMap(entry.getValue());

            Map<String, Object> toVertex = parentVertex;
            if (group.toEdge()) {
                toVertex = current.getVertex();
            }
            /*String _key = Getter.get("_key", toVertex);
            if (_key.equals("L3KFL54944A834757515A1D521C6854F")){
                JSONUtils.println(toVertex);
            }*/

            // aggEdge
            Map<String, Object> aggEdge = new HashMap<>();
            String edgeSchema = group.getEdgeTable();
            aggEdge.put(GKeys.SCHEMA, edgeSchema);
            aggEdge.put(GKeys.FROM, group.getFrom());
            aggEdge.put(GKeys.TO, group.getTo());
            aggEdge.put(GKeys.TYPE, GdbRule.AGG_EDGE);
            aggEdge.put(GKeys.COUNT, groupEdges.size());
            aggEdge.put(GKeys.ID, makeAggEdgeId(schema, group.getFrom(), group.getTo()));
            aggEdges.add(aggEdge);

            // label
            String scriptResult = executeEdgeScript(edgeScript, toVertex, groupEdges);
            String labelResult = parseAndMakeAggLabel(edgeLabel, scriptResult, aggEdge);
            aggEdge.put(GKeys.LABEL, labelResult);
        }
        return aggEdges;
    }

    private static String executeEdgeScript(String script, Map<String, Object> toVertex, List<Map<String, Object>>
            groupEdges) {
        String source = script;
        String vertexSchema = Getter.get(GKeys.SCHEMA, toVertex);
        Map<String, Object> context = new HashMap<>();
        List<RElement> scriptElements = parseExpression(source);
        for (RElement element : scriptElements) {
            String table = element.getTable();
            String field = element.getField();
            String expr = element.getExpression();
            String exprEscape = element.getEscapeExpression();
            source = source.replace(expr, exprEscape);

            if (vertexSchema.equals(table)) {
                Object elementValue = toVertex.get(field);
                context.put(exprEscape, elementValue);
            } else {
                List<Object> values = new ArrayList<>();
                for (Map<String, Object> edge : groupEdges) {
                    if (!edge.containsKey(field)) {
                        /*LOG.info(JSON.toJSONString(toVertex, true));
                        throw new IllegalArgumentException(MessageFormat
                                .format("missing field[{0}/{1}]", table, field));*/
                        LOG.warn(MessageFormat.format("missing field[{0}/{1}]", table, field));
                        continue;
                    }
                    Object elementValue = edge.get(field);
                    values.add(elementValue);
                }
                context.put(exprEscape, values);
            }
        }
        Object scriptResult = runner.execute(source, context);
        if (Rule.INVALID_RESULT.equals(scriptResult)) {
            //throw new IllegalArgumentException("edgeScript invalid result");
            scriptResult = "";
            LOG.error("edge script invalid result,context is {0}", context);
        }
        if (scriptResult instanceof Number) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(2);
            scriptResult = nf.format(scriptResult);
        }
        return String.valueOf(scriptResult);
    }

    private static void parseAndMakeEdgeLabel(Map<String, GdbRule> rule,
                                              Map<String, Object> edge,
                                              String edgeSchema) {
        GdbRule edgeRule = rule.get(edgeSchema);
        if (edgeRule == null) {
            throw new IllegalArgumentException("edge rule missing with " + edgeSchema);
        }
        String edgeLabel = edgeRule.getLabel();
        List<RElement> elements = parseExpression(edgeLabel);
        for (RElement element : elements) {
            element.resetValue();
            switch (element.getType()) {
                case FIELD:
                    String elementValue = Getter.get(element.getField(), edge);
                    element.setStringValue(elementValue);
                    break;
            }
        }
        edge.put(GKeys.LABEL, makeLabel(edgeLabel, elements));
    }

    private static String parseAndMakeAggLabel(String edgeLabel, String scriptResult, Map<String, Object> aggEdge) {
        List<RElement> elements = parseExpression(edgeLabel);
        for (RElement element : elements) {
            element.resetValue();
            String express = element.getExpression();
            switch (element.getType()) {
                case VALUE:
                    if (express.equals(GdbRule.EL_EDGE_SCRIPT)){
                        element.setStringValue(Objects.toString(scriptResult, ""));
                        break;
                    }
                    express = StringUtils.substringBetween(express, "(", ")");
                    switch (express){
                        case GKeys.COUNT:
                            element.setStringValue(Objects.toString(aggEdge.get(GKeys.COUNT), ""));
                            break;
                    }
                    break;
            }
        }
        return makeLabel(edgeLabel, elements);
    }

    private static List<RElement> parseExpression(String expr) {
        if (expr == null) {
            return Collections.emptyList();
        }
        List<RElement> resultList = new ArrayList<>();
        Matcher matcher = EL_ELEMENT_PATTERN.matcher(expr);
        while (matcher.find()) {
            String str = matcher.group();
            RElement element = new RElement();
            element.setExpression(str);
            if (str.toLowerCase().startsWith("@field")) {
                element.setType(RElement.Type.FIELD);
                str = StringUtils.substringBetween(str, "(", ")");
                element.setTable(StringUtils.substringBefore(str, "."));
                element.setField(StringUtils.substringAfter(str, "."));
            } else if (str.toLowerCase().startsWith("@value")) {
                element.setType(RElement.Type.VALUE);
            }
            resultList.add(element);
        }
        return resultList;
    }

    private static String makeLabel(String label, List<RElement> elements) {
        for (RElement element : elements) {
            String expr = element.getExpression();
            expr = expr.replace("(", "\\(").replace(")", "\\)").replace(".", "\\.");
            label = label.replaceAll(expr, element.getStringValue());
        }
        return label;
    }

    private static void doRebuildVertex(GTreeNode parent, GTreeNode current, GdbAtlasQo searchQo) {
        Map<String, GdbRule> rule = searchQo.getRule();
        Map<String, Object> vertex = current.getVertex();
        String schema = Getter.get(GKeys.SCHEMA, vertex);
        if (StringUtils.isBlank(schema) || schema.startsWith(GdbRule.NEW_VERTEX)) {
            return;
        }
        GdbRule vertexRule = rule.get(schema);
        if (vertexRule == null) {
            vertex.put(GKeys.LABEL, null);
            return;
//            throw new IllegalArgumentException("vertex rule missing with " + schema);
        }
        String vertexLabel = vertexRule.getLabel();
        List<RElement> elements = parseExpression(vertexLabel);
        for (RElement element : elements) {
            //element.resetValue();
            if (element.getType() == RElement.Type.FIELD) {
                String elementValue = Getter.get(element.getField(), vertex);
                element.setStringValue(elementValue);
                element.setStringValue(elementValue);
            }
        }
        String vertexLabelResult = makeLabel(vertexLabel, elements);
        if (parent != null) {
            GTreeNode child = parent.getChild(current);
            child.getVertex().put(GKeys.LABEL, vertexLabelResult);
        }
        vertex.put(GKeys.LABEL, vertexLabelResult);
    }

    private static String makeAggEdgeId(String schema, String from, String to) {
        return schema + "/" + CodecUtils.md5(schema + from + to);
    }
}
