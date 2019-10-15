package com.haizhi.graph.dc.inbound.task.conf;

import com.haizhi.graph.dc.core.constant.ExecutionType;
import com.haizhi.graph.dc.core.constant.OperateType;
import com.haizhi.graph.dc.core.model.po.DcTaskPo;
import lombok.Data;
import lombok.NonNull;

/**
 * Created by chengangxiong on 2019/02/14
 */
@Data
public class DcTask {

    private Long taskId;

    private DcTaskPo dcTaskPo;

    private OperateType OperateType;

    private ExecutionType executionType;

    private String cron;

    private Integer errorMode;

    public DcTask(@NonNull DcTaskPo dcTaskPo) {
        this.dcTaskPo = dcTaskPo;
        this.taskId = dcTaskPo.getId();
        this.OperateType = dcTaskPo.getOperateType();
        this.cron = dcTaskPo.getCron();
        this.executionType = dcTaskPo.getExecutionType();
        this.errorMode = dcTaskPo.getErrorMode();
    }
}
