package com.haizhi.graph.server.api.gdb.search.aggregation;

import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;

/**
 * Created by chengmo on 2018/1/22.
 */
public abstract class AbstractAggBuilder<AB extends AbstractAggBuilder<AB>> implements AggBuilder{

    public static final String TABLE_FIELD = "table";
    public static final String FIELD_FIELD = "field";
    public static final String SIZE_FIELD = "size";

    protected final String name;
    protected String table;
    protected String field;
    protected int size = 30;

    protected AggBuilder subAggregation;

    protected AbstractAggBuilder(String table, String field) {
        this(table + "." + field, table, field);
    }

    protected AbstractAggBuilder(String name, String table, String field) {
        if (name == null) {
            throw new IllegalArgumentException("[name] must not be null: [" + name + "]");
        }
        if (table == null) {
            throw new IllegalArgumentException("[table] must not be null: [" + name + "]");
        }
        if (field == null) {
            throw new IllegalArgumentException("[field] must not be null: [" + name + "]");
        }
        this.name = name;
        this.table = table;
        this.field = field;
    }

    @Override
    public AB subAggregation(AggBuilder aggBuilder) {
        if (aggBuilder == null) {
            throw new IllegalArgumentException("[aggregation] must not be null: [" + name + "]");
        }
        subAggregation = aggBuilder;
        return (AB) this;
    }

    @Override
    public XContentBuilder toXContent() {
        return doXContent();
    }

    protected abstract XContentBuilder doXContent();

    protected AB table(String table) {
        if (table == null) {
            throw new IllegalArgumentException("[table] must not be null: [" + name + "]");
        }
        this.table = table;
        return (AB) this;
    }

    protected AB field(String field) {
        if (field == null) {
            throw new IllegalArgumentException("[field] must not be null: [" + name + "]");
        }
        this.field = field;
        return (AB) this;
    }

    protected AB size(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("[size] must be greater than 0. Found [" + size + "] in [" + name + "]");
        }
        this.size = size;
        return (AB) this;
    }

    protected String field() {
        return field;
    }

    protected int size() {
        return size;
    }
}
