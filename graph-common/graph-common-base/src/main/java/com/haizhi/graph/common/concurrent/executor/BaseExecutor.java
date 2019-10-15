package com.haizhi.graph.common.concurrent.executor;

import com.haizhi.graph.common.concurrent.threadpool.GExecutors;

import java.util.concurrent.Executor;

/**
 * Created by chengmo on 2018/2/5.
 */
public class BaseExecutor {

    protected final Executor executor;

    public BaseExecutor() {
        this.executor = GExecutors.newDefaultFixed();
    }
}
