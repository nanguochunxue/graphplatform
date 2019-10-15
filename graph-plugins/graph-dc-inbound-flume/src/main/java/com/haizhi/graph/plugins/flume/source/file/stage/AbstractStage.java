package com.haizhi.graph.plugins.flume.source.file.stage;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;

import java.util.concurrent.Callable;

/**
 * Created by chengmo on 2018/12/13.
 */
public abstract class AbstractStage implements Callable<Long> {
    private static final GLog LOG = LogFactory.getLogger(AbstractStage.class);
    private boolean isRunning;

    @Override
    public Long call() throws Exception {
        Long eventCount = 0L;
        try {
            eventCount = doCall();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        } finally {
        }
        return eventCount;
    }

    protected abstract Long doCall() throws Exception ;
}
