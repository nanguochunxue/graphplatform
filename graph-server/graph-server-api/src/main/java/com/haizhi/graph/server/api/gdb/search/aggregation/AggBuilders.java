package com.haizhi.graph.server.api.gdb.search.aggregation;

/**
 * Created by chengmo on 2018/1/22.
 */
public class AggBuilders {

    public static TermAggBuilder term(String table, String field) {
        return term(table + "." + field, table, field);
    }

    public static TermAggBuilder term(String name, String table, String field) {
        return new TermAggBuilder(name, table, field);
    }

    public static RangeAggBuilder range(String table, String field) {
        return range(table + "." + field, table, field);
    }

    public static RangeAggBuilder range(String name, String table, String field) {
        return new RangeAggBuilder(name, table, field);
    }
}
