package com.haizhi.graph.server.api.gdb.search.query;

import java.util.Collection;

/**
 * Created by chengmo on 2018/1/20.
 */
public class QBuilders {

    public static GraphQBuilder graphQBuilder(Collection<String> vertexTables, Collection<String> edgeTables) {
        return new GraphQBuilder(vertexTables, edgeTables);
    }

    public static BoolQBuilder boolQBuilder() {
        return new BoolQBuilder();
    }

    public static VertexQBuilder vertexQBuilder(String vertexName, AbstractQBuilder query) {
        return new VertexQBuilder(vertexName, query);
    }

    public static VertexQBuilder vertexQBuilder(String vertexName, String vertexAlias, AbstractQBuilder query) {
        return new VertexQBuilder(vertexName, vertexAlias, query);
    }

    public static EdgeQBuilder edgeQBuilder(String edgeName, AbstractQBuilder query) {
        return new EdgeQBuilder(edgeName, query);
    }

    public static EdgeQBuilder edgeQBuilder(String edgeName, String edgeAlias, AbstractQBuilder query) {
        return new EdgeQBuilder(edgeName, edgeAlias, query);
    }

    public static TermQBuilder termQBuilder(String fieldName, Object value) {
        return new TermQBuilder(fieldName, value);
    }

    public static TermsQBuilder termsQBuilder(String fieldName, Iterable<?> values) {
        return new TermsQBuilder(fieldName, values);
    }

    public static TermsQBuilder termsQBuilder(String fieldName, String... values) {
        return new TermsQBuilder(fieldName, values);
    }

    public static TermsQBuilder termsQBuilder(String fieldName, int... values) {
        return new TermsQBuilder(fieldName, values);
    }

    public static TermsQBuilder termsQBuilder(String fieldName, long... values) {
        return new TermsQBuilder(fieldName, values);
    }

    public static TermsQBuilder termsQBuilder(String fieldName, float... values) {
        return new TermsQBuilder(fieldName, values);
    }

    public static TermsQBuilder termsQBuilder(String fieldName, double... values) {
        return new TermsQBuilder(fieldName, values);
    }

    public static LikeQBuilder likeQBuilder(String fieldName, Object value) {
        return new LikeQBuilder(fieldName, value);
    }

    public static RangeQBuilder rangeQBuilder(String fieldName) {
        return new RangeQBuilder(fieldName);
    }

    public static IsNullQBuilder isNullQBuilder(String fieldName){
        return new IsNullQBuilder(fieldName);
    }

    public static IsNullQBuilder isNullQBuilder(String table, String fieldName){
        return new IsNullQBuilder(table, fieldName);
    }
}
