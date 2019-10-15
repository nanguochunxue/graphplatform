package com.haizhi.graph.common.concurrent.action;

import java.util.concurrent.Callable;

/**
 * Created by chengmo on 2018/2/5.
 */
public abstract class AbstractCallable<V> implements Callable<V> {

    @Override
    public V call() throws Exception {
        try {
            return execute();
        } catch (Throwable t) {
            onFailure(t);
        } finally {
            onAfter();
        }
        return null;
    }

    public void onAfter() {
        // nothing by default
    }

    public void onRejection(Throwable t) {
        onFailure(t);
    }

    public abstract void onFailure(Throwable t);

    protected abstract V execute() throws Exception;
}
