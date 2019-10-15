package com.haizhi.graph.tag.analytics.task;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.tag.analytics.bean.TagContext;
import com.haizhi.graph.tag.analytics.task.context.TaskContext;
import com.haizhi.graph.tag.analytics.task.scheduler.*;

import java.util.Set;

/**
 * Created by chengmo on 2018/3/13.
 */
public class DagTaskExecutor extends TaskExecutor {

    private static final GLog LOG = LogFactory.getLogger(DagTaskExecutor.class);
    private static final String NAME = "TAG.DAG";

    @Override
    public void execute(TaskContext taskContext){
        if (isRunning){
            LOG.warn("Dag task is already running.");
            return;
        }
        this.isRunning = true;
        try {
            TagContext ctx = this.getTagContext(taskContext);
            JobRunner jobRunner = new JobRunnerImpl();
            boolean success = jobRunner.waitForCompletion(ctx);
            LOG.info("Dag task execution result: success={0}", success);
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
        Set<Long> tagIds = taskContext.getTagIds();
        // TagContext
        TagContext ctx = this.createTagContext(taskContext);
        // Partitions
        if (taskContext.isPartitionsEnabled()){
            ctx.setPartitions(taskContext.getPartitions());
        }
        // StageSet
        StageSet stageSet = StageSetManager.get(tagIds, ctx.getTagDomain(), ctx.getDomain());
        ctx.setStageSet(stageSet);
        // FlowTaskBuilder
        ctx.setTaskIdPrefix(NAME);
        FlowTaskBuilder.build(ctx);
        return ctx;
    }
}
