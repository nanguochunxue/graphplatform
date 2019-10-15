package com.haizhi.graph.server.api.gdb.search.query;

import com.haizhi.graph.server.api.gdb.search.bean.ParseFields;
import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by chengmo on 2018/1/20.
 */
public class VertexQBuilder extends AbstractQBuilder {

    public static final String NAME = "vertex";
    private final String vertexName;
    private final String vertexAlias;
    private final QBuilder query;
    private final int maxDepth;

    public VertexQBuilder(String vertexName, QBuilder query) {
        this(vertexName, vertexName, query, 1);
    }

    public VertexQBuilder(String vertexName, String vertexAlias, QBuilder query) {
        this(vertexName, vertexAlias, query, 1);
    }

    public VertexQBuilder(String vertexName, String vertexAlias, QBuilder query, int maxDepth) {
        this.vertexName = requireValue(vertexName, "[" + NAME + "] requires 'vertexName' field");
        this.query = requireValue(query, "[" + NAME + "] requires 'query' field");
        this.vertexAlias = StringUtils.isBlank(vertexAlias) ? vertexName : vertexAlias;
        this.maxDepth = maxDepth;
    }

    @Override
    protected XContentBuilder doXContent() {
        XContentBuilder xb = XContentBuilder.builder(NAME);
        xb.put(ParseFields.ALIAS_FIELD, vertexAlias);
        xb.put(vertexName, query.toXContent().rootObject());
        return xb;
    }

    public QBuilder getQuery() {
        return query;
    }
}
