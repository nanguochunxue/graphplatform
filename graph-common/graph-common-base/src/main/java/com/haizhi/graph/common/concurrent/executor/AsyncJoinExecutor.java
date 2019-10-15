package com.haizhi.graph.common.concurrent.executor;

import com.haizhi.graph.common.concurrent.action.ActionListener;
import com.haizhi.graph.common.concurrent.action.ActionSupplier;
import com.haizhi.graph.common.concurrent.action.Futures;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by chengmo on 2018/2/5.
 */
public class AsyncJoinExecutor<T> extends BaseExecutor {

    private static final GLog LOG = LogFactory.getLogger(AsyncJoinExecutor.class);

    protected Map<String, ActionSupplier<T>> suppliers = new LinkedHashMap<>();
    protected int taskIdCounter = 0;
    protected long timeout;

    public AsyncJoinExecutor() {
    }

    /**
     * Added a asynchronous task.
     *
     * @param task
     */
    public void join(ActionListener<T> task) {
        this.join(null, task);
    }

    /**
     * Added a asynchronous task with id.
     *
     * @param taskId
     * @param task
     */
    public void join(String taskId, ActionListener<T> task) {
        this.check(task);
        suppliers.put(keyGenerator(taskId), new ActionSupplier<T>(task));
    }

    /**
     * Timeout unit: seconds.
     *
     * @param timeout
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Returns execution result when all of the given task complete.
     *
     * @return
     */
    public Map<String, T> actionGet() {
        try {
            // supply
            List<CompletableFuture<T>> futures = new ArrayList<>();
            for (ActionSupplier<T> supplier : suppliers.values()) {
                CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return supplier.call();
                    } catch (Exception e) {
                        LOG.error(e);
                    }
                    return null;
                }, executor);
                futures.add(future);
            }

            // wait for completion
            CompletableFuture<List<T>> all = Futures.sequenceCombine(futures);
            List<T> results;
            if (timeout > 0) {
                results = all.get(timeout, TimeUnit.SECONDS);
            } else {
                results = all.get();
            }
            return getResultMap(results);
        } catch (TimeoutException e) {
            LOG.error("Execute timeout[{0}s]", timeout);
        } catch (Exception e) {
            LOG.error(e);
        }
        return Collections.emptyMap();
    }

    /**
     * Dispose
     */
    public void dispose() {
        suppliers.clear();
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private void check(ActionListener<T> task) {
        Objects.requireNonNull(task);
        if (suppliers.size() == 100) {
            throw new IllegalArgumentException("too many tasks limit 100");
        }
    }

    private Map<String, T> getResultMap(List<T> results) {
        int resultSize = results.size();
        int size = suppliers.size();
        if (resultSize != size) {
            LOG.error("Results size[{0}] not equal to suppliers size[{1}]", resultSize, size);
            return Collections.emptyMap();
        }

        Map<String, T> resultMap = new LinkedHashMap<>();
        int index = 0;
        for (String taskId : suppliers.keySet()) {
            resultMap.put(taskId, results.get(index));
            index++;
        }
        return resultMap;
    }

    private String keyGenerator(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            taskId = String.valueOf(++taskIdCounter);
        }
        return taskId;
    }
}
