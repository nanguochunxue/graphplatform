package com.haizhi.graph.dc.inbound.task;

import com.haizhi.graph.dc.inbound.task.conf.DcTask;
import org.quartz.SchedulerException;

/**
 * Created by chengangxiong on 2019/02/14
 */
public interface TaskManager {

    void submit(DcTask dcTask) throws SchedulerException;

    void runOnce(DcTask dcTask) throws SchedulerException;

    void stop(Long taskId) throws SchedulerException;

    void pause(Long taskId) throws SchedulerException;

    void resume(DcTask dcTask) throws SchedulerException;
}
