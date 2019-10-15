package com.haizhi.graph.common.concurrent.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by chengmo on 2018/2/5.
 */
public class GThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * Name used in error reporting.
     */
    private final String name;

    GThreadPoolExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                        BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append('[');
        b.append(name).append(", ");
        if (getQueue() instanceof SizeBlockingQueue) {
            @SuppressWarnings("rawtypes")
            SizeBlockingQueue queue = (SizeBlockingQueue) getQueue();
            b.append("queue capacity = ").append(queue.capacity()).append(", ");
        }
        b.append(super.toString()).append(']');
        return b.toString();
    }
}
