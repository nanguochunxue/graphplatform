package com.haizhi.graph.server.api.gdb.search.query;

/**
 * Created by chengmo on 2018/1/20.
 */
public class LikeQBuilder extends TermQBuilder {

    public static final String NAME = "like";

    public LikeQBuilder(String fieldName, Object value) {
        super(fieldName, value);
    }
}
