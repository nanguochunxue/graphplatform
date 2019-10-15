package com.haizhi.graph.tag.analytics.engine.driver;


import com.haizhi.graph.tag.analytics.engine.conf.FlowTask;

/**
 * Created by chengmo on 2018/4/4.
 */
public abstract class DriverRunner {

    public abstract Object doRun(FlowTask task);
}
