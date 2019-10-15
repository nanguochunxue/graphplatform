package com.haizhi.graph.server.api.gdb.search.query;

import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;

/**
 * Created by chengmo on 2018/1/20.
 */
public class TermQBuilder extends AbstractQBuilder {

    public static final String NAME = "term";
    public static final String VALUE_FIELD = "value";
    private final String fieldName;
    private final Object value;

    public TermQBuilder(String fieldName, Object value) {
        this.fieldName = requireValue(fieldName, "field name is null or empty");
        this.value = requireValue(value, "value cannot be null");
    }

    @Override
    protected XContentBuilder doXContent() {
        XContentBuilder xb = XContentBuilder.builder(NAME);
        xb.put(fieldName, XContentBuilder.jsonObject().fluentPut(VALUE_FIELD, value));
        return xb;
    }

    public String fieldName() {
        return this.fieldName;
    }

    public Object value() {
        return this.value;
    }
}
