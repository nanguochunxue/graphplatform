package com.haizhi.graph.tag.analytics.cron;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.tag.analytics.task.IncTaskExecutor;
import com.haizhi.graph.tag.analytics.task.TaskExecutor;
import com.haizhi.graph.tag.analytics.task.context.TaskContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by chengmo on 2018/3/13.
 */
@Component
public class IncCronJob {

    private static final GLog LOG = LogFactory.getLogger(IncCronJob.class);

    @Value("${tag.analytics.domains:}")
    private String domains;
    @Value("${tag.analytics.task.inc.enabled:}")
    private boolean enabled;

    private boolean isRunning;

    @Scheduled(cron = "${tag.analytics.task.inc.cron:0 30 0 * * ?}")
    public void run(){
        if (isRunning){
            return;
        }
        if (!enabled){
            LOG.info("[Incremental-cron-job] it is not enabled");
            return;
        }
        this.isRunning = true;
        LOG.info("[Incremental-cron-job] starting...");
        try {
            String[] arr = StringUtils.split(domains, ",");
            for (int i = 0; i < arr.length; i++) {
                String graphName = arr[i];
                TaskExecutor taskExecutor = new IncTaskExecutor();
                TaskContext ctx = new TaskContext(graphName);
                taskExecutor.execute(ctx);
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        this.isRunning = false;
        LOG.info("[Incremental-cron-job] has bean completed.");
    }
}
