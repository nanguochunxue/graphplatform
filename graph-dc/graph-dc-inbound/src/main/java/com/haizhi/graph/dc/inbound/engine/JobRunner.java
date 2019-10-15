package com.haizhi.graph.dc.inbound.engine;

import com.haizhi.graph.dc.inbound.engine.conf.DcFlowTask;
import com.haizhi.graph.engine.flow.action.ActionListener;

/**
 * Created by chengangxiong on 2019/02/14
 */
public interface JobRunner {

    boolean waitForCompletion(DcFlowTask task);

    boolean waitForCompletion(DcFlowTask task, ActionListener listener);
}
