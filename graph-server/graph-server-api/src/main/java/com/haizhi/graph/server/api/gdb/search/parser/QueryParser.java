package com.haizhi.graph.server.api.gdb.search.parser;

import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;

import java.util.List;

/**
 * Created by chengmo on 2018/1/23.
 */
public interface QueryParser {

    /**
     * To query expression, returns graph SQL if builder is GraphQBuilder.
     *
     * @param builder
     * @return
     */
    String toQuery(XContentBuilder builder);

    /**
     * returns multiple graph SQLs if builder is GraphQBuilder.
     *
     * @author thomas
     * @param builder
     * @return
     */
    List<String> toMultiQuery(XContentBuilder builder);

    /**
     * To query expression for where condition.
     *
     * @param builder gdbQuery.getQuery().toXContent()
     * @return
     */
    String toQueryExpression(XContentBuilder builder);
}
