package com.haizhi.graph.common.concurrent.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chengmo on 2018/2/5.
 */
public class GExecutors {

    /**
     * Useful for testing
     */
    public static final String DEFAULT_SYS_PROP = "graph.processors.override";

    private static final String DEFAULT_THREAD_PREFIX = "global";
    private static final int DEFAULT_QUEUE_SIZE = 1000;

    /**
     * Returns the number of processors available but at most 32.
     * This is used to adjust thread pools sizes.
     */
    public static int boundedNumberOfProcessors() {
        int defaultValue = Math.min(32, Runtime.getRuntime().availableProcessors());
        try {
            defaultValue = Integer.parseInt(System.getProperty(DEFAULT_SYS_PROP));
        } catch (Throwable ignored) {
        }
        return defaultValue;
    }

    public static GThreadPoolExecutor newDefaultFixed() {
        int availableProcessors = boundedNumberOfProcessors();
        int size = ((availableProcessors * 3) / 2) + 1;
        final ThreadFactory threadFactory = GExecutors.daemonThreadFactory(DEFAULT_THREAD_PREFIX);
        return newFixed(DEFAULT_THREAD_PREFIX, size, DEFAULT_QUEUE_SIZE, threadFactory);
    }

    public static GThreadPoolExecutor newFixed(String name, int size, int queueCapacity, ThreadFactory threadFactory) {
        BlockingQueue<Runnable> queue;
        if (queueCapacity < 0) {
            queue = new LinkedTransferQueue<>();
        } else {
            queue = new SizeBlockingQueue<>(new LinkedTransferQueue<>(), queueCapacity);
        }
        return new GThreadPoolExecutor(name, size, size, 0, TimeUnit.MILLISECONDS, queue, threadFactory);
    }

    public static ThreadFactory daemonThreadFactory(String namePrefix) {
        return new GThreadFactory(namePrefix);
    }

    static class GThreadFactory implements ThreadFactory {
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;
        final boolean daemon;

        public GThreadFactory(String namePrefix) {
            this(namePrefix, true);
        }

        public GThreadFactory(String namePrefix, boolean daemon) {
            this.namePrefix = namePrefix;
            this.daemon = daemon;
            SecurityManager s = System.getSecurityManager();
            this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + "[T#" + threadNumber.getAndIncrement() + "]", 0);
            t.setDaemon(true);
            return t;
        }

        public ThreadGroup getThreadGroup() {
            return this.group;
        }
    }

    /**
     * Cannot instantiate.
     */
    private GExecutors() {
    }
}
