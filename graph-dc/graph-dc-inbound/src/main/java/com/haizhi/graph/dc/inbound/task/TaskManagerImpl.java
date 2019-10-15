package com.haizhi.graph.dc.inbound.task;

import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.redis.RedisService;
import com.haizhi.graph.dc.common.monitor.MonitorService;
import com.haizhi.graph.dc.common.service.TaskRedisService;
import com.haizhi.graph.dc.core.service.DcStoreParamService;
import com.haizhi.graph.dc.core.constant.ExecutionType;
import com.haizhi.graph.dc.core.constant.InboundConstant;
import com.haizhi.graph.dc.inbound.engine.JobRunner;
import com.haizhi.graph.dc.inbound.service.DcTaskInstanceService;
import com.haizhi.graph.dc.inbound.service.DcTaskMetaService;
import com.haizhi.graph.dc.inbound.service.DcTaskService;
import com.haizhi.graph.dc.inbound.task.conf.DcTask;
import com.haizhi.graph.dc.inbound.task.quartz.ScheduledTaskJob;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import com.haizhi.graph.sys.core.config.service.SysConfigService;
import com.haizhi.graph.sys.file.service.SysFileService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * Created by chengangxiong on 2019/02/14
 */
@Component
public class TaskManagerImpl implements TaskManager {

    private static final GLog LOG = LogFactory.getLogger(TaskManagerImpl.class);

    public static final String JOB_GROUP_PRE = InboundConstant.QUARTZ_JOB_GROUP_PRE;
    public static final String JOB_NAME_PRE = InboundConstant.QUARTZ_JOB_NAME_PRE;

    private Scheduler scheduler;

    @Autowired
    private DcTaskService dcTaskService;

    @Autowired
    private DcTaskInstanceService dcTaskInstanceService;

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private DcStoreParamService dcStoreParamService;

    @Autowired
    private DcTaskMetaService dcTaskMetaService;

    @Autowired
    private StoreUsageService storeUsageService;

    @Autowired
    private JobRunner jobRunner;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TaskRedisService taskRedisService;

    public TaskManagerImpl() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            throw new UnexpectedStatusException("init quartz fail", e);
        }
    }

    @Override
    public void submit(DcTask dcTask) throws SchedulerException {
        JobDetail jobDetail = createJob(dcTask);
        Trigger trigger = createTrigger(dcTask);
        scheduler.deleteJob(jobDetail.getKey());
        scheduler.scheduleJob(jobDetail, trigger);
    }

    @Override
    public void stop(Long taskId) throws SchedulerException {
        JobKey jobKey = findJobKey(taskId);
        scheduler.interrupt(jobKey);
    }

    @Override
    public void pause(Long taskId) throws SchedulerException {
        JobKey jobKey = findJobKey(taskId);
        scheduler.deleteJob(jobKey);
    }

    @Override
    public void resume(DcTask dcTask) throws SchedulerException {
        submit(dcTask);
    }

    @Override
    public void runOnce(DcTask dcTask) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(JobKey.createUniqueName(JOB_GROUP_PRE + dcTask.getTaskId()));
        JobDetail jobDetail = createJob(dcTask, jobKey);
        Trigger trigger = getOnceTrigger();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    @PreDestroy
    public void preDestroy() throws SchedulerException {
        scheduler.shutdown();
    }

    private JobDetail createJob(DcTask dcTask) {
        JobKey jobKey = findJobKey(dcTask.getTaskId());
        return createJob(dcTask, jobKey);
    }

    private JobDetail createJob(DcTask dcTask, JobKey jobKey) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(InboundConstant.JOB_DATA_DCTASK, dcTask);
        jobDataMap.put(InboundConstant.JOB_DATA_METRIC_SERVICE, monitorService);
        jobDataMap.put(InboundConstant.JOB_DATA_JOB_RUNNER, jobRunner);
        jobDataMap.put(InboundConstant.JOB_DATA_TASK_SERVICE, dcTaskService);
        jobDataMap.put(InboundConstant.JOB_DATA_INSTANCE_SERVICE, dcTaskInstanceService);
        jobDataMap.put(InboundConstant.JOB_DATA_FILE_SERVICE, sysFileService);
        jobDataMap.put(InboundConstant.JOB_DATA_STORE_PARAM_SERVICE, dcStoreParamService);
        jobDataMap.put(InboundConstant.JOB_DATA_TASK_META_SERVICE, dcTaskMetaService);
        jobDataMap.put(InboundConstant.JOB_DATA_STORE_USAGE, storeUsageService);
        jobDataMap.put(InboundConstant.JOB_DATA_SYS_CONFIG, sysConfigService);
        jobDataMap.put(InboundConstant.JOB_DATA_REDIS_SERVICE, redisService);
        jobDataMap.put(InboundConstant.JOB_DATA_TASK_REDIS_SERVICE, taskRedisService);
        JobDetail jobDetail = JobBuilder.newJob(ScheduledTaskJob.class)
                .withIdentity(jobKey)
                .storeDurably(true)
                .usingJobData(jobDataMap).build();
        return jobDetail;
    }

    private JobKey findJobKey(Long taskId) {
        String jobGroup = JOB_GROUP_PRE + taskId;
        String jobName = JOB_NAME_PRE + taskId;
        return JobKey.jobKey(jobName, jobGroup);
    }

    private Trigger getOnceTrigger() {
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger().startNow();
        return triggerBuilder.build();
    }

    private Trigger createTrigger(DcTask dcTask) {
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger().startNow();
        if (dcTask.getExecutionType() == ExecutionType.CRON) {
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(dcTask.getCron()));
        }
        return triggerBuilder.build();
    }
}
