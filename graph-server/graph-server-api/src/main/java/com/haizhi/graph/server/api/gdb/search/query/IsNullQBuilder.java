package com.haizhi.graph.server.api.gdb.search.query;

import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;

/**
 * Created by chengmo on 2018/1/20.
 */
public class IsNullQBuilder extends AbstractQBuilder {

    public static final String NAME = "is_null";
    public static final String TABLE_FIELD = "table";
    public static final String FIELD_FIELD = "field";
    private final String table;
    private final String fieldName;

    public IsNullQBuilder(String fieldName) {
        this("$", fieldName);
    }

    public IsNullQBuilder(String table, String fieldName) {
        this.table = requireValue(table, "table is null or empty");
        this.fieldName = requireValue(fieldName, "field name is null or empty");
    }

    @Override
    protected XContentBuilder doXContent() {
        XContentBuilder xb = XContentBuilder.builder(NAME);
        xb.put(TABLE_FIELD, table);
        xb.put(FIELD_FIELD, fieldName);
        return xb;
    }

    public String table(){
        return table;
    }

    public String fieldName() {
        return this.fieldName;
    }
}
