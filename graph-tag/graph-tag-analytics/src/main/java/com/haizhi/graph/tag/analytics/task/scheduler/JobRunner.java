package com.haizhi.graph.tag.analytics.task.scheduler;

import com.haizhi.graph.tag.analytics.bean.TagContext;

import java.util.Set;

/**
 * Created by chengmo on 2018/4/9.
 */
public interface JobRunner {

    boolean waitForCompletion(TagContext ctx);

    Set<String> getFailedTasks();
}
