package com.haizhi.graph.tag.analytics.controller;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.bean.Result;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.tag.analytics.model.TaskReq;
import com.haizhi.graph.tag.analytics.task.DagTaskExecutor;
import com.haizhi.graph.tag.analytics.task.FullTaskExecutor;
import com.haizhi.graph.tag.analytics.task.IncTaskExecutor;
import com.haizhi.graph.tag.analytics.task.TaskExecutor;
import com.haizhi.graph.tag.analytics.task.context.TaskContext;
import com.haizhi.graph.tag.analytics.task.context.TaskType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Created by chengmo on 2018/3/13.
 */
@Api(description = "[任务]启动")
@RestController
@RequestMapping("/task")
public class TaskController extends BaseController {

    private static final GLog LOG = LogFactory.getLogger(TaskController.class);

    @Value("#{'${tag.analytics.domains:}'.split(',')}")
    private Set<String> domains;
    private TaskType taskType;
    private AsyncTaskThread asyncTask;

    @ApiOperation(value = "任务启动", notes = "无")
    @RequestMapping(value = "start", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Result startTask(@RequestBody TaskReq params) {
        Result result = new Result();
        if (!paramsValidator(params)) {
            return this.setMessage("Invalid task type or graph params", result);
        }
        return this.doStartTask(params);
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private synchronized Result doStartTask(TaskReq params) {
        Result result = new Result();
        if (asyncTask != null && asyncTask.isRunning()) {
            return this.setMessage("Task[tag-analytics] is already running.", result);
        }
        asyncTask = new AsyncTaskThread();
        asyncTask.setParams(taskType, params);
        asyncTask.start();
        this.setMessage(taskType + " task[tag-analytics] has been started.", result);
        return result;
    }

    private Result setMessage(String message, Result result) {
        LOG.info(message);
        result.setMessage(message);
        return result;
    }

    private boolean paramsValidator(TaskReq params) {
        LOG.info(JSON.toJSONString(params));
        this.taskType = TaskType.fromName(params.getType());
        if (taskType == null || !domains.contains(params.getGraph())) {
            return false;
        }
        if (taskType != TaskType.DAG) {
            return true;
        }
        Set<Long> tagIds = params.getTagIds();
        if (tagIds == null || tagIds.isEmpty()) {
            return false;
        }
        return true;
    }

    private static class AsyncTaskThread extends Thread {
        private TaskType taskType;
        private TaskReq params;
        private boolean isRunning;

        public void setParams(TaskType taskType, TaskReq params) {
            this.taskType = taskType;
            this.params = params;
        }

        public boolean isRunning() {
            return isRunning;
        }

        @Override
        public void run() {
            this.isRunning = true;
            doRun();
            this.isRunning = false;
        }

        private void doRun() {
            LOG.info("{0} task[tag-analytics] is starting...", taskType);
            TaskExecutor taskExecutor;
            TaskContext ctx = new TaskContext(params.getGraph());
            ctx.setTaskType(taskType);
            try {
                switch (taskType) {
                    case DAG:
                        taskExecutor = new DagTaskExecutor();
                        ctx.addTagIds(params.getTagIds());
                        ctx.addPartitions(params.getFilter());
                        taskExecutor.setDebugLauncher(params.isDebugLauncher());
                        taskExecutor.execute(ctx);
                        break;
                    case FULL:
                        taskExecutor = new FullTaskExecutor();
                        ctx.setPartitionsEnabled(params.isUseCache());
                        ctx.addPartitions(params.getFilter());
                        taskExecutor.execute(ctx);
                        break;
                    case INC:
                        taskExecutor = new IncTaskExecutor();
                        taskExecutor.setDebugLauncher(params.isDebugLauncher());
                        taskExecutor.execute(ctx);
                        break;
                }
            } catch (Exception e) {
                LOG.error(e);
            } finally {
                LOG.info("{0} task[tag-analytics] is completed.", taskType);
            }
        }
    }
}
