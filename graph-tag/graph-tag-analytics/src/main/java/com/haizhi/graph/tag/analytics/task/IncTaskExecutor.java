package com.haizhi.graph.tag.analytics.task;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.tag.analytics.bean.TagContext;
import com.haizhi.graph.tag.analytics.task.context.TaskContext;
import com.haizhi.graph.tag.analytics.task.scheduler.*;

/**
 * Created by chengmo on 2018/3/26.
 */
public class IncTaskExecutor extends TaskExecutor {

    private static final GLog LOG = LogFactory.getLogger(IncTaskExecutor.class);
    private static final String NAME = "TAG.INC";

    @Override
    public void execute(TaskContext taskContext) {
        if (isRunning){
            LOG.warn("Increment task is already running.");
            return;
        }
        this.isRunning = true;
        try {
            TagContext ctx = this.getTagContext(taskContext);
            JobRunner jobRunner = new JobRunnerImpl();
            boolean success = jobRunner.waitForCompletion(ctx);
            LOG.info("Increment task execution result: success={0}", success);
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
        taskContext.addPartitions(this.getDefaultPartitions());
        // TagContext
        TagContext ctx = this.createTagContext(taskContext);
        // Partitions
        ctx.setPartitions(taskContext.getPartitions());
        // StageSet
        StageSet stageSet = StageSetManager.get(ctx.getTagDomain(), ctx.getDomain());
        ctx.setStageSet(stageSet);
        // FlowTaskBuilder
        ctx.setTaskIdPrefix(NAME);
        FlowTaskBuilder.build(ctx);
        return ctx;
    }
}
