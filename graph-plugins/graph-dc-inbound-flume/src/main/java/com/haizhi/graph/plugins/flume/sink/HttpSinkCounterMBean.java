package com.haizhi.graph.plugins.flume.sink;

/**
 * Created by chengangxiong on 2018/12/27
 */
public interface HttpSinkCounterMBean {

    long getHttpSuccessCount();

    long getHttpFailCount();

    long getHttpSuccessTimeCost();
}
