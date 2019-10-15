package com.haizhi.graph.dc.inbound.util;

import com.haizhi.graph.dc.core.constant.TaskInstanceState;
import com.haizhi.graph.dc.core.constant.TaskState;
import com.haizhi.graph.dc.core.model.po.DcTaskInstancePo;

/**
 * Created by chengangxiong on 2019/04/25
 */
public class InboundUtil {

    public static final String NORMAL = "NORMAL";

    public static final String READY = "READY";

    public static final String SUCCESS = "SUCCESS";

    public static final String INTERRUPTED = "INTERRUPTED";

    public static final String EXPORTING = "EXPORTING";

    public static final String RUNNING = "RUNNING";

    public static final String PAUSED = "PAUSED";

    public static final String FAILED = "FAILED";

    public static final String getState(TaskInstanceState instanceState) {
        if (instanceState == TaskInstanceState.RUNNING)
            return RUNNING;
        if (instanceState == TaskInstanceState.SUCCESS)
            return SUCCESS;
        if (instanceState == TaskInstanceState.INTERRUPTED)
            return INTERRUPTED;
        if (instanceState == TaskInstanceState.EXPORTING)
            return EXPORTING;
        if (instanceState == TaskInstanceState.FAILED)
            return FAILED;
        /*if (instanceState == TaskInstanceState.READY)
            return READY;*/
        return READY;
    }

    public static final String getState(TaskState taskState, DcTaskInstancePo instancePo) {
        if (taskState == TaskState.NORMAL)
            return NORMAL;
        if (taskState == TaskState.PAUSED)
            return PAUSED;
        if (instancePo != null && instancePo.getState() != null) {
            return getState(instancePo.getState());
        }
        return NORMAL;
    }

    public static final boolean checkPathLegal(String path) {
        return path.toLowerCase().endsWith(".json") || path.toLowerCase().endsWith(".csv");
    }

}
