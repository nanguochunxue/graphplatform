package com.haizhi.graph.server.api.gdb.search.aggregation;

import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;

/**
 * Created by chengmo on 2018/1/22.
 */
public class TermAggBuilder extends AbstractAggBuilder {

    public static final String NAME = "term";

    public TermAggBuilder(String table, String field) {
        super(table, field);
    }

    public TermAggBuilder(String name, String table, String field) {
        super(name, table, field);
    }

    @Override
    protected XContentBuilder doXContent() {
        XContentBuilder xb = XContentBuilder.builder(this.name);
        xb.put(NAME, XContentBuilder.jsonObject()
                .fluentPut(TABLE_FIELD, table)
                .fluentPut(FIELD_FIELD, field)
                .fluentPut(SIZE_FIELD, size));
        return xb;
    }
}
