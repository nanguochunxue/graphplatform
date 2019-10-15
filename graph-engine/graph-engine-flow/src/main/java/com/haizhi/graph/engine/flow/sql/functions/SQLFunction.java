package com.haizhi.graph.engine.flow.sql.functions;

/**
 * Created by chengmo on 2018/4/3.
 */
public abstract class SQLFunction {

    public abstract String rebuild(String sql);

    public abstract String getParam();
}
