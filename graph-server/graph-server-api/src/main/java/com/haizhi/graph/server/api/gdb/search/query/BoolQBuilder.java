package com.haizhi.graph.server.api.gdb.search.query;

import com.alibaba.fastjson.JSONArray;
import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengmo on 2018/1/19.
 */
public class BoolQBuilder extends AbstractQBuilder {

    public static final String NAME = "bool";
    public static final String MUST = "must";
    public static final String MUST_NOT = "must_not";
    public static final String SHOULD = "should";

    private final List<QBuilder> mustClauses = new ArrayList<>();
    private final List<QBuilder> mustNotClauses = new ArrayList<>();
    private final List<QBuilder> shouldClauses = new ArrayList<>();

    public BoolQBuilder must(QBuilder builder) {
        if (builder != null) {
            mustClauses.add(builder);
        }
        return this;
    }

    public BoolQBuilder mustNot(QBuilder builder) {
        if (builder != null) {
            mustNotClauses.add(builder);
        }
        return this;
    }

    public BoolQBuilder should(QBuilder builder) {
        if (builder != null) {
            shouldClauses.add(builder);
        }
        return this;
    }

    @Override
    protected XContentBuilder doXContent() {
        XContentBuilder xb = XContentBuilder.builder(NAME);
        xb.put(MUST, doXArrayContent(mustClauses));
        xb.put(MUST_NOT, doXArrayContent(mustNotClauses));
        xb.put(SHOULD, doXArrayContent(shouldClauses));
        return xb;
    }

    private static JSONArray doXArrayContent(List<QBuilder> clauses) {
        JSONArray array = XContentBuilder.jsonArray();
        for (QBuilder clause : clauses) {
            array.add(clause.toXContent().rootObject());
        }
        return array;
    }
}
