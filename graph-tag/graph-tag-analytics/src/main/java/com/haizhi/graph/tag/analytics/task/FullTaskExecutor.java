package com.haizhi.graph.tag.analytics.task;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.DateUtils;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.engine.flow.tools.hdfs.HDFSHelper;
import com.haizhi.graph.tag.analytics.bean.TagContext;
import com.haizhi.graph.tag.analytics.task.context.TaskContext;
import com.haizhi.graph.tag.analytics.task.scheduler.*;
import com.haizhi.graph.tag.analytics.util.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/4/9.
 */
public class FullTaskExecutor extends TaskExecutor {

    private static final GLog LOG = LogFactory.getLogger(DagTaskExecutor.class);
    private static final String NAME = "TAG.FULL";
    private static final String LAST_EXECUTION_DATE_FIELD = "last.execution.date";
    private static final String FAILED_TASKS_FIELD = "failed.tasks";

    @Override
    public void execute(TaskContext taskContext) {
        if (isRunning){
            LOG.warn("Full task is already running.");
            return;
        }
        this.isRunning = true;
        try {
            TagContext ctx = this.getTagContext(taskContext);
            JobRunner jobRunner = new JobRunnerImpl();
            boolean success = jobRunner.waitForCompletion(ctx);
            LOG.info("Full task execution result: success={0}", success);
            this.afterFinished(jobRunner);
        } catch (Exception e) {
            LOG.error(e);
        } finally {
            this.isRunning = false;
        }
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private TagContext getTagContext(TaskContext taskContext) {
        this.buildPartitions(taskContext);
        // TagContext
        TagContext ctx = this.createTagContext(taskContext);
        // Partitions
        if (taskContext.isPartitionsEnabled()){
            ctx.setPartitions(taskContext.getPartitions());
        }
        // StageSet
        StageSet stageSet = StageSetManager.get(ctx.getTagDomain(), ctx.getDomain());
        ctx.setStageSet(stageSet);
        // FlowTaskBuilder
        ctx.setTaskIdPrefix(NAME);
        FlowTaskBuilder.build(ctx);
        return ctx;
    }

    private void buildPartitions(TaskContext taskContext){
        HDFSHelper helper = new HDFSHelper();
        String text = helper.readLine(Constants.PATH_FULL_TASK);
        helper.close();
        if (StringUtils.isBlank(text)){
            return;
        }
        Map<String, Object> config = JSON.parseObject(text, Map.class);
        String lastExecutionDate = Getter.get(LAST_EXECUTION_DATE_FIELD, config);
        taskContext.addRangePartition(Constants.PARTITION_DAY, lastExecutionDate, null);
    }

    private void afterFinished(JobRunner jobRunner){
        Map<String, Object> config = new HashMap<>();
        config.put(LAST_EXECUTION_DATE_FIELD, DateUtils.getYesterday());
        config.put(FAILED_TASKS_FIELD, jobRunner.getFailedTasks());

        HDFSHelper helper = new HDFSHelper();
        helper.upsertLine(Constants.PATH_FULL_TASK, JSON.toJSONString(config));
        helper.close();
    }
}
