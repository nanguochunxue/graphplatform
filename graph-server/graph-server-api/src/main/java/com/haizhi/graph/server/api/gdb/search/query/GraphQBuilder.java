package com.haizhi.graph.server.api.gdb.search.query;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.server.api.gdb.search.bean.Sort;
import com.haizhi.graph.server.api.gdb.search.result.Projections;
import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by chengmo on 2018/1/22.
 */
public class GraphQBuilder extends AbstractQBuilder {

    public static final String NAME = "graph";
    public static final String VERTEX_TABLES_FIELD = "vertex_tables";
    public static final String EDGE_TABLES_FIELD = "edge_tables";
    public static final String START_VERTICES_FIELD = "start_vertices";
    public static final String END_VERTICES_FIELD = "end_vertices";
    public static final String MAX_DEPTH_FIELD = "max_depth";
    public static final String DIRECTION_FIELD = "direction";
    public static final String OFFSET_FIELD = "offset";
    public static final String SIZE_FIELD = "size";
    public static final String SORT_FIELD = "sort";
    public static final String QUERY_FIELD = "query";
    public static final String PROJECTIONS_FIELD = "projections";

    private Set<String> vertexTables = new LinkedHashSet<>();
    private Set<String> edgeTables = new LinkedHashSet<>();
    private Set<String> startVertices = new LinkedHashSet<>();
    private Set<String> endVertices = new LinkedHashSet<>();
    private QBuilder query;
    private Integer maxDepth = 2;
    private String direction = "ANY";
    private Integer offset = 0;
    private Integer size = 10;
    private Sort sort = new Sort();
    private Projections projections = new Projections();

    public GraphQBuilder(Collection<String> vertexTables, Collection<String> edgeTables) {
        if (vertexTables == null || vertexTables.isEmpty()) {
            throw new IllegalArgumentException("No vertex tables specified for graph query");
        }
        if (edgeTables == null || edgeTables.isEmpty()) {
            throw new IllegalArgumentException("No edge tables specified for graph query");
        }
        this.vertexTables.addAll(vertexTables);
        this.edgeTables.addAll(edgeTables);
    }

    @Override
    protected XContentBuilder doXContent() {
        XContentBuilder xb = XContentBuilder.builder(NAME);
        xb.put(VERTEX_TABLES_FIELD, XContentBuilder.jsonArray().fluentAddAll(vertexTables));
        xb.put(EDGE_TABLES_FIELD, XContentBuilder.jsonArray().fluentAddAll(edgeTables));
        xb.put(START_VERTICES_FIELD, XContentBuilder.jsonArray().fluentAddAll(startVertices));
        xb.put(END_VERTICES_FIELD, XContentBuilder.jsonArray().fluentAddAll(endVertices));
        if(maxDepth != null) xb.put(MAX_DEPTH_FIELD, maxDepth);
        if(direction != null) xb.put(DIRECTION_FIELD, direction);
        if(offset != null) xb.put(OFFSET_FIELD, offset);
        if(size != null) xb.put(SIZE_FIELD, size);
        if(sort != null) xb.put(SORT_FIELD, (JSONObject) JSON.toJSON(sort));

        if (query != null) {
            xb.put(QUERY_FIELD, query.toXContent().rootObject());
        }

        if(projections != null)
            xb.put(PROJECTIONS_FIELD, (JSONObject) JSON.toJSON(projections));

        return xb;
    }

    public GraphQBuilder startVertices(Collection<String> startVertices) {
        if (startVertices != null) {
            this.startVertices.addAll(startVertices);
        }
        return this;
    }

    public GraphQBuilder endVertices(Collection<String> endVertices) {
        if (endVertices != null) {
            this.endVertices.addAll(endVertices);
        }
        return this;
    }

    public GraphQBuilder setQuery(QBuilder query) {
        this.query = query;
        return this;
    }

    public GraphQBuilder maxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public GraphQBuilder direction(String direction) {
        this.direction = direction;
        return this;
    }

    public GraphQBuilder offset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public GraphQBuilder size(Integer size) {
        this.size = size;
        return this;
    }

    public GraphQBuilder sort(Sort sort) {
        this.sort = sort;
        return this;
    }

    public GraphQBuilder addSortOrder(String property, Sort.Direction direction) {
        this.sort.add(new Sort.Order(property, direction));
        return this;
    }

    public GraphQBuilder projections(Projections projections) {
        this.projections = projections;
        return this;
    }

    public QBuilder query() {
        return query;
    }

    public int maxDepth() {
        return maxDepth;
    }
}
