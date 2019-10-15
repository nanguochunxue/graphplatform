package com.haizhi.graph.tag.analytics.task.scheduler;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.haizhi.graph.common.context.SpringContext;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.engine.flow.action.ActionLauncher;
import com.haizhi.graph.engine.flow.action.spark.SparkActionLauncher;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.engine.flow.tools.hdfs.HDFSHelper;
import com.haizhi.graph.tag.analytics.bean.TagContext;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTask;
import com.haizhi.graph.tag.analytics.util.Constants;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by chengmo on 2018/4/9.
 */
public class JobRunnerImpl implements JobRunner {

    private static final GLog LOG = LogFactory.getLogger(JobRunnerImpl.class);
    private static final int BATCH_MAX_SIZE = 5;
    private static final int BATCH_TIMEOUT = 5; //hour

    private int batchMaxSize;
    private int batchTimeout;
    private ExecutorService executor;
    private Map<String, Future<Boolean>> futures;
    private HDFSHelper hdfsHelper = new HDFSHelper();

    private Deque<Stage.Task> priorityQueue = new ArrayDeque<>();
    private Set<String> runningTasks = new LinkedHashSet<>();
    private Set<String> succeededTasks = new LinkedHashSet<>();
    private Set<String> failedTasks = new LinkedHashSet<>();

    public JobRunnerImpl() {
        this.initialize();
    }

    @Override
    public boolean waitForCompletion(TagContext ctx) {
        boolean success = true;
        StageSet stageSet = ctx.getStageSet();
        Iterables.addAll(priorityQueue, stageSet.getTasks().values());
        int batchNo = 0;
        while (hasNext()) {
            try {
                // Batch executing
                List<Stage.Task> tasks = this.nextBatch();
                executor = Executors.newFixedThreadPool(tasks.size());
                for (Stage.Task task : tasks) {
                    // upload FlowTask to hdfs
                    String flowTaskId = task.getFlowTask().getId();
                    String path = MessageFormat.format(Constants.PATH_FLOW_TASK, flowTaskId);
                    hdfsHelper.upsertLine(path, JSON.toJSONString(task.getFlowTask()));

                    // submit task
                    TaskSubmitter submitter = new TaskSubmitter(task, ctx.getProperties());
                    Future<Boolean> completionFuture = executor.submit(submitter);
                    String taskId = task.getTaskId();
                    futures.put(taskId, completionFuture);
                    runningTasks.add(taskId);
                    task.setState(Stage.TaskState.RUNNING);
                }
                try {
                    executor.shutdown();
                    if (executor.awaitTermination(batchTimeout, TimeUnit.HOURS)){
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                }
                batchNo++;
                runningTasks.clear();
                LOG.info("BatchTasks[{0}-{1}] has been completed!", batchNo, tasks.size());

                // Result
                Set<String> currentFailedTasks = new LinkedHashSet<>();
                for (Map.Entry<String, Future<Boolean>> entry : futures.entrySet()) {
                    String taskId = entry.getKey();
                    Boolean isSucceeded = entry.getValue().get();
                    if (isSucceeded) {
                        succeededTasks.add(taskId);
                        stageSet.changeState(taskId, Stage.TaskState.SUCCEEDED);
                    } else {
                        failedTasks.add(taskId);
                        currentFailedTasks.add(taskId);
                        stageSet.changeState(taskId, Stage.TaskState.FAILED);
                    }
                }
                futures.clear();

                // Refresh priorityQueue
                refreshPriorityQueue(currentFailedTasks, ctx);
            } catch (Exception e) {
                success = false;
                LOG.error(e);
            }
        }
        LOG.info("Failed tasks: {0}", failedTasks);
        return success;
    }

    @Override
    public Set<String> getFailedTasks() {
        return failedTasks;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private void refreshPriorityQueue(Set<String> failedTasks, TagContext ctx) {
        if (failedTasks.isEmpty()) {
            return;
        }
        Set<String> needToRemoveTaskIds = StageSetManager.getSuccessors(failedTasks, ctx);
        Set<Stage.Task> tasks = Sets.newLinkedHashSet(priorityQueue);
        priorityQueue.clear();
        for (Stage.Task task : tasks) {
            if (!needToRemoveTaskIds.contains(task.getTaskId())) {
                priorityQueue.offerFirst(task);
            }
        }
    }

    private boolean hasNext() {
        return !priorityQueue.isEmpty();
    }

    private List<Stage.Task> nextBatch() {
        List<Stage.Task> tasks = new ArrayList<>();
        int stageId = 0;
        for (int i = 0; i < batchMaxSize; i++) {
            Stage.Task task = priorityQueue.pollFirst();
            if (task == null) {
                break;
            }
            // 同一阶段的放在一个批次
            if (stageId > 0 && task.getStageId() > stageId) {
                priorityQueue.offerFirst(task);
                break;
            }
            stageId = task.getStageId();
            tasks.add(task);
        }
        return tasks;
    }

    private static class TaskSubmitter implements Callable<Boolean> {

        private Stage.Task task;
        private Map<String, String> conf;

        public TaskSubmitter(Stage.Task task, Map<String, String> conf) {
            this.task = task;
            this.conf = conf;
        }

        @Override
        public Boolean call() throws Exception {
            try {
                ActionLauncher launcher = new SparkActionLauncher(this.getActionConfig());
                return launcher.waitForCompletion();
            } catch (Exception e) {
                LOG.error(e);
            }
            return false;
        }

        private Map<String, String> getActionConfig() {
            Map<String, String> config = new HashMap<>();
            config.putAll(conf);
            FlowTask flowTask = task.getFlowTask();
            config.put(FKeys.APP_NAME, flowTask.getId());
            config.putAll(flowTask.getProperties());

            // app args
            //String flowTaskJson = JSON.toJSONString(flowTask);
            //config.put(FKeys.APP_ARGS, flowTaskJson.replaceAll("}}", "} }"));
            String appArgs = MessageFormat.format(Constants.PATH_FLOW_TASK, flowTask.getId());
            if (flowTask.isSecurityEnabled()){
                appArgs += FKeys.SEPARATOR_002;
                appArgs += flowTask.getUserPrincipal() + FKeys.SEPARATOR_001 + flowTask.getUserConfPath();
            }
            config.put(FKeys.APP_ARGS, appArgs);
            return config;
        }
    }

    private void initialize() {
        try {
            this.batchMaxSize = NumberUtils.toInt(
                    SpringContext.getProperty(Constants.SCHEDULER_BATCH_MAX_SIZE), BATCH_MAX_SIZE);
            this.batchTimeout = NumberUtils.toInt(
                    SpringContext.getProperty(Constants.SCHEDULER_BATCH_TIMEOUT), BATCH_TIMEOUT);
            this.futures = new LinkedHashMap<>();
        } catch (Exception e) {
            LOG.error(e);
        }
    }
}
