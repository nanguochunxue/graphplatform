package com.haizhi.graph.server.api.gdb.search.query;

import com.haizhi.graph.server.api.gdb.search.bean.ParseFields;
import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by chengmo on 2018/1/20.
 */
public class EdgeQBuilder extends AbstractQBuilder {

    public static final String NAME = "edge";
    private final String edgeName;
    private final String edgeAlias;
    private final QBuilder query;

    public EdgeQBuilder(String edgeName, QBuilder query) {
        this(edgeName, edgeName, query);
    }

    public EdgeQBuilder(String edgeName, String edgeAlias, QBuilder query) {
        this.edgeName = requireValue(edgeName, "[" + NAME + "] requires 'edgeName' field");
        this.query = requireValue(query, "[" + NAME + "] requires 'query' field");
        this.edgeAlias = StringUtils.isBlank(edgeAlias) ? edgeName : edgeAlias;
    }

    @Override
    protected XContentBuilder doXContent() {
        XContentBuilder xb = XContentBuilder.builder(NAME);
        xb.put(ParseFields.ALIAS_FIELD, edgeAlias);
        xb.put(edgeName, query.toXContent().rootObject());
        return xb;
    }

    public String getEdgeName() {
        return edgeName;
    }

    public QBuilder getQuery() {
        return query;
    }

}
