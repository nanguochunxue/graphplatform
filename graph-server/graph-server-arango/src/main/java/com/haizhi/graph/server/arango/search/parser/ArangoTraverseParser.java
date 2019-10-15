package com.haizhi.graph.server.arango.search.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.server.api.gdb.search.bean.CollectionType;
import com.haizhi.graph.server.api.gdb.search.bean.Sort;
import com.haizhi.graph.server.api.gdb.search.query.GraphQBuilder;
import com.haizhi.graph.server.api.gdb.search.query.VertexQBuilder;
import com.haizhi.graph.server.api.gdb.search.result.Projection;
import com.haizhi.graph.server.api.gdb.search.result.Projections;
import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by thomas on 18/1/26.
 */
@Service
public class ArangoTraverseParser extends ArangoQueryParser {
    protected static final ThreadLocal<Set<String>> VERTEX_TABLES_THREAD_LOCAL = ThreadLocal.withInitial(HashSet::new);
    protected static final ThreadLocal<Set<String>> EDGE_TABLES_THREAD_LOCAL = ThreadLocal.withInitial(HashSet::new);
    protected static final String CURRENT_ID = "CURRENT._id";
    protected static final String CURRENT_FROM = "CURRENT._from";
    protected static final String CURRENT_TO = "CURRENT._to";
    protected static final String CURRENT_NODE = "{{node}}";
    protected static final String CURRENT = "CURRENT";
    protected static final ThreadLocal<Set<String>> START_VERTEX_THREAD_LOCAL = ThreadLocal.withInitial(HashSet::new);
    //特定实体集合，针对所有实体规则免疫（实体filter、输出实体范围）【一般数据来源为K层展开的起点，全路径的起点和终点，离线查询的所有输入实体】
    protected static final ThreadLocal<Set<String>> SPE_VERTEX_THREAD_LOCAL = ThreadLocal.withInitial(HashSet::new);

    /**
     * basic collection-level filter
     *
     * @return
     */
    protected String makeCollectionLevelFilter(String field, Collection<String> collections) {
        if (CollectionUtils.isEmpty(collections)) return "";
        if (collections.size() > 1)
            return "SUBSTRING({{field}}, 0, FIND_FIRST({{field}}, '/')) IN ['{{collections}}']"
                    .replace("{{field}}", field)
                    .replace("{{collections}}", StringUtils.join(collections, "', '"));
        return String.format("IS_SAME_COLLECTION('%s', %s)", collections.iterator().next(), field);
    }

    /**
     * 构造完整filter条件
     * 注意：特定实体范围内的集合，将对实体filter、输出实体范围免疫。实现逻辑在变量parsedFilterExpr中（构造实体filter）和fromCollectionLevelFilter、toCollectionLevelFilter（输出实体范围）
     * @param graph
     * @return
     */
    protected String makeFilterAql(JSONObject graph) {
        //filter expression
        JSONObject query = graph.getJSONObject(GraphQBuilder.QUERY_FIELD);
        String parsedFilterExpr = parseGraphQuery(query);

        //输出实体和输出边的范围
        String edgeCollectionLevelFilter = makeCollectionLevelFilter(CURRENT_ID, EDGE_TABLES_THREAD_LOCAL.get());
        String fromCollectionLevelFilter = makeCollectionLevelFilter(CURRENT_FROM, VERTEX_TABLES_THREAD_LOCAL.get());
        String toCollectionLevelFilter = makeCollectionLevelFilter(CURRENT_TO, VERTEX_TABLES_THREAD_LOCAL.get());

        StringBuilder sb = new StringBuilder("FILTER true");
        if (StringUtils.isNotBlank(parsedFilterExpr)) {
            sb.append(" AND ").append(parsedFilterExpr);
        }
        if (StringUtils.isNotBlank(edgeCollectionLevelFilter)) {
            sb.append(" AND ").append(edgeCollectionLevelFilter);
        }
        //实体范围不用剔除起点
        if (StringUtils.isNotBlank(fromCollectionLevelFilter)) {
            sb.append(String.format(" AND ((%s) or (%s)) ", fromCollectionLevelFilter, speFilterNode(CURRENT_FROM)));
        }
        if (StringUtils.isNotBlank(toCollectionLevelFilter)) {
            sb.append(String.format(" AND ((%s) or (%s)) ", toCollectionLevelFilter, speFilterNode(CURRENT_TO)));
        }
        return sb.toString();
    }

    protected String makeEndVerticesFilter(List<String> endVertices) {
        if (CollectionUtils.isEmpty(endVertices)) return "";
        return String.format("FILTER LAST(p.vertices)._id IN ['%s']", StringUtils.join(endVertices, "', '"));
    }

    /**
     * parse to get multiple traverse AQLs. Each start vertex has its traverse AQL
     *
     * @param builder
     * @return
     */
    @Override
    public List<String> toMultiQuery(XContentBuilder builder) {
        VERTEX_TABLES_THREAD_LOCAL.get().clear();
        EDGE_TABLES_THREAD_LOCAL.get().clear();
        START_VERTEX_THREAD_LOCAL.get().clear();
        SPE_VERTEX_THREAD_LOCAL.get().clear();

        JSONObject object = builder.rootObject();
        if (!preCheck(object)) return null;

        JSONObject graph = object.getJSONObject(GraphQBuilder.NAME);
        List<String> startVertices = graph.getJSONArray(GraphQBuilder.START_VERTICES_FIELD).stream().filter(Objects::nonNull).map(jsonObj -> (String) jsonObj).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(startVertices)) {
            LOGGER.warn("empty start vertices. return null");
            return null;
        } else {
            START_VERTEX_THREAD_LOCAL.get().addAll(startVertices);
        }

        SPE_VERTEX_THREAD_LOCAL.get().addAll(startVertices);
        JSONArray endVerticesJsonArray = graph.getJSONArray(GraphQBuilder.END_VERTICES_FIELD);
        List<String> endVertices = null;
        if (!CollectionUtils.isEmpty(endVerticesJsonArray)) {
            endVertices = endVerticesJsonArray.stream().filter(Objects::nonNull).map(jsonObj -> (String) jsonObj).collect(Collectors.toList());
            SPE_VERTEX_THREAD_LOCAL.get().addAll(endVertices);
        }

        List<String> vertexTables = graph.getJSONArray(GraphQBuilder.VERTEX_TABLES_FIELD).stream().filter(Objects::nonNull).map(obj -> (String) obj).collect(Collectors.toList());
        List<String> edgeTables = graph.getJSONArray(GraphQBuilder.EDGE_TABLES_FIELD).stream().filter(Objects::nonNull).map(obj -> (String) obj).collect(Collectors.toList());

        String direction = graph.getString(GraphQBuilder.DIRECTION_FIELD);
        Integer offset = graph.getInteger(GraphQBuilder.OFFSET_FIELD);
        Integer size = graph.getInteger(GraphQBuilder.SIZE_FIELD);
        Integer maxDepth = graph.getInteger(GraphQBuilder.MAX_DEPTH_FIELD);
        Sort sort = graph.getObject(GraphQBuilder.SORT_FIELD, Sort.class);
        Projections projections = graph.getObject(GraphQBuilder.PROJECTIONS_FIELD, Projections.class);
        if (!CollectionUtils.isEmpty(projections.getProjections())) {
            List<String> vertexProjections = projections.getProjections().values().stream().flatMap(List::stream).filter(projection -> projection.getCollectionType().equals(CollectionType.DOCUMENT)).map(Projection::getTable).collect(Collectors.toList());
            List<String> edgeProjections = projections.getProjections().values().stream().flatMap(List::stream).filter(projection -> projection.getCollectionType().equals(CollectionType.EDGE)).map(Projection::getTable).collect(Collectors.toList());
            VERTEX_TABLES_THREAD_LOCAL.get().addAll(vertexProjections);
            EDGE_TABLES_THREAD_LOCAL.get().addAll(edgeProjections);
        } else {
            VERTEX_TABLES_THREAD_LOCAL.get().addAll(vertexTables);
            EDGE_TABLES_THREAD_LOCAL.get().addAll(edgeTables);
        }

        String filterExpr = makeFilterAql(graph);
        String endVerticesFilter = makeEndVerticesFilter(endVertices);

        String traverseExpr = "WITH {{with}}\n" +
                "FOR start IN ['{{startVertices}}']\n" +
                "FOR v,e,p IN 1..{{maxDepth}}\n" +
                "{{direction}} start\n" +
                "{{edges}}\n" +
                "options {bfs:true,uniqueVertices:'path'}\n" +
                "\n" +
                "{{endVerticesFilter}}\n" +
                "LET newEdges=p.edges[\n" +
                "* {{filter}}\n" +
                "return CURRENT\n" +
                "]\n" +
                "\n" +
                "FILTER LENGTH(newEdges) > 0 AND LENGTH(newEdges) == LENGTH(p.edges)\n" +
                "{{limit}}\n" +
                "{{sort}}\n" +
                "RETURN p";
        traverseExpr = traverseExpr.replace("{{with}}", StringUtils.join(vertexTables, ", "))
                .replace("{{direction}}", StringUtils.isEmpty(direction) ? DEFAULT_DIRECTION : direction)
                .replace("{{maxDepth}}", maxDepth != null ? maxDepth + "" : DEFAULT_MAX_DEPTH + "")
                .replace("{{edges}}", StringUtils.join(edgeTables, ", "))
                .replace("{{filter}}", filterExpr)
                .replace("{{endVerticesFilter}}", endVerticesFilter);
        //limit
        String limitExpr = "";
        if (offset != null && size != null && offset >= 0 && size > 0)
            limitExpr = "LIMIT {{offset}}, {{size}}".replace("{{offset}}", offset + "").replace("{{size}}", size + "");
        traverseExpr = traverseExpr.replace("{{limit}}", limitExpr);

        //sort
        String sortExpr = "";
        if (!Sort.isEmpty(sort)) sortExpr = "SORT " + sort.parse();
        traverseExpr = traverseExpr.replace("{{sort}}", sortExpr);
        return Collections.singletonList(traverseExpr.replace("{{startVertices}}", StringUtils.join(startVertices, "', '")));
    }

    /**
     * do nothing. see {@link #toQuery(XContentBuilder)}
     *
     * @param object
     * @return
     */
    @Override
    protected String doQuery(JSONObject object) {
        return null;
    }

    /**
     * parse to get one single traverse AQL。<br/>
     * theoretically，with multiple start vertices，come multiple traverse AQLs. <br/>
     * HOWEVER, this methods returns only the first one. <br/>
     * if parameter 'builder' contains multiple start vertices, please use {@link #toMultiQuery(XContentBuilder)}, unless you know what you're doing
     *
     * @param builder
     * @return
     */
    @Override
    public String toQuery(XContentBuilder builder) {
        List<String> lists = toMultiQuery(builder);
        if (!CollectionUtils.isEmpty(lists)) {
            if (lists.size() > 1)
                LOGGER.warn("multi traverse AQLs detected! return the first one. Please ensure this is what you want.");
            return lists.get(0);
        }
        return null;
    }

    /**
     * @param collection arangodb table name
     * @param prefix     vertex or edge prefix
     * @return
     */
    private String isSameCollectionAql(String prefix, String collection) {
        return String.format("%s != null AND is_same_collection(%s, '%s')", prefix, prefix, collection);
    }
//
//    @Override
//    protected String parseGraphTable(JSONObject object, String builderName) {
//        JSONObject table = object.getJSONObject(builderName);
//        String tableName = new ArrayList<>(table.keySet()).get(1);
//        String expression = parseObjectTree(table.getJSONObject(tableName));
//
//        if (builderName.equals(VertexQBuilder.NAME)) {
//            String fromFilter = makeCollectionLevelFilter(CURRENT_FROM, Collections.singletonList(tableName));
//            String toFilter = makeCollectionLevelFilter(CURRENT_TO, Collections.singletonList(tableName));
//            String fromExpr = expression.replaceAll("\\$", String.format("DOCUMENT(%s)", CURRENT_FROM));
//            String toExpr = expression.replaceAll("\\$", String.format("DOCUMENT(%s)", CURRENT_TO));
//            fromFilter = String.format("(%s AND %s)", fromFilter, fromExpr);
//            toFilter = String.format("(%s AND %s)", toFilter, toExpr);
//            return String.format("(%s AND %s)", fromFilter, toFilter);
//        } else {
//            List<String> edgeTables = new ArrayList<>(EDGE_TABLES_THREAD_LOCAL.get());
//            edgeTables.remove(tableName);
//            String inOtherSchemasExpr = makeCollectionLevelFilter(CURRENT_ID, edgeTables);
//            String collectionLevelFilter = makeCollectionLevelFilter(CURRENT_ID, Collections.singletonList(tableName));
//            expression = expression.replaceAll("\\$", CURRENT);
//            expression = String.format("(%s AND %s)", collectionLevelFilter, expression);
//            if (StringUtils.isNotBlank(inOtherSchemasExpr)) {
//                expression = String.format("(%s AND %s)", expression, inOtherSchemasExpr);
//            }
//            return expression;
//        }
//    }

    /**
     * 实体或边的过滤条件（每一个filter）
     * @param object
     * @param builderName
     * @return
     */
    @Override
    protected String parseGraphTable(JSONObject object, String builderName) {
        JSONObject table = object.getJSONObject(builderName);
        String tableName = new ArrayList<>(table.keySet()).get(1);
        String expression = parseObjectTree(table.getJSONObject(tableName));

        if (builderName.equals(VertexQBuilder.NAME)) {
            //实体规则
            String nodeFilter = makeCollectionLevelFilter(CURRENT_NODE, Collections.singletonList(tableName));
            String nodeExpr = expression.replaceAll("\\$", String.format("DOCUMENT(%s)", CURRENT_NODE));
            String speFilterNode = speFilterNode(CURRENT_NODE);
            return String.format("(%s OR (!%s OR (%s AND %s)))", speFilterNode, nodeFilter, nodeFilter, nodeExpr);
        } else {
            //关系规则
            //List<String> edgeTables = new ArrayList<>(EDGE_TABLES_THREAD_LOCAL.get());
            //edgeTables.remove(tableName);
            //String inOtherSchemasExpr = makeCollectionLevelFilter(CURRENT_ID, edgeTables);
            String collectionLevelFilter = makeCollectionLevelFilter(CURRENT_ID, Collections.singletonList(tableName));
            expression = expression.replaceAll("\\$", CURRENT);
            expression = String.format("(!%s OR (%s AND %s))", collectionLevelFilter, collectionLevelFilter, expression);
            /*if (StringUtils.isNotBlank(inOtherSchemasExpr)) {
                expression = String.format("(%s AND %s)", expression, inOtherSchemasExpr);
            }*/
            return expression;
        }
    }

    @Override
    protected String parseGraphQuery(JSONObject query) {
        List<String> vertexTables = new ArrayList<>(VERTEX_TABLES_THREAD_LOCAL.get());
        LOGGER.info("vertexTables={0}", vertexTables.toString());
        LOGGER.info("parseObjectTree query: {0}", JSON.toJSONString(query, true));
        String expr = parseObjectTree(query);

        if (!StringUtils.isEmpty(expr)) {
            expr = String.format("(%s and %s)",expr.replace("{{node}}",CURRENT_FROM),expr.replace("{{node}}",CURRENT_TO));
            /*for (String vertexTable : vertexTables) {
                if (!expr.contains(vertexTable)) {
                    LOGGER.info("filter vertex {0}, {1}", vertexTable, START_VERTEX_THREAD_LOCAL.get().toString());
                    String fromInOtherSchemasExpr = otherFilter(vertexTable, CURRENT_FROM, CURRENT_TO);
                    String toInOtherSchemasExpr = otherFilter(vertexTable, CURRENT_TO, CURRENT_FROM);
                    builder.append(BL).append(OR).append(BL).append("((");
                    builder.append(fromInOtherSchemasExpr);
                    builder.append(")").append(BL);
                    builder.append(OR);
                    builder.append(BL).append("(");
                    builder.append(toInOtherSchemasExpr).append("))");
                }
            }*/

        }
        expr = prettyFormat(expr);
        return expr;
    }

    /**
     * 过滤规则中只有一种类型的实体时，其他类型的实体须全部呈现，只有判断其_from或_to为搜索的点
     * 如只对Person过滤，则Company全部呈现
     *
     * @param vertexTable
     * @param from
     * @param to
     * @return
     */
    private static String otherFilter(String vertexTable, String from, String to) {
        String str = String.format("IS_SAME_COLLECTION('%s', %s) AND DOCUMENT(%s)._id=='{{startVertices}}'", vertexTable, from, to);
        return str;
    }

    /**
     * 如果实体在特定的范围内，将不适用实体过滤规则和输出实体规则
     * @param nodeId
     * @return
     */
    private static String speFilterNode(String nodeId) {
        return String.format("%s in ['%s']", nodeId, StringUtils.join(SPE_VERTEX_THREAD_LOCAL.get(),"','"));
    }

    public static void main(String[] args) {
        System.out.println(otherFilter("Company", CURRENT_FROM, CURRENT_TO));
    }

}

