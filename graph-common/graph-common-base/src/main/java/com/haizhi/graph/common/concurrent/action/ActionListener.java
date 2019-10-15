package com.haizhi.graph.common.concurrent.action;

/**
 * Created by chengmo on 2018/2/5.
 */
public interface ActionListener<Response> {

    /**
     * Executed when an action occurs.
     */
    Response doExecute();
}
