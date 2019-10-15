package com.haizhi.graph.dc.core.model.vo;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.BaseVo;
import com.haizhi.graph.dc.core.constant.ExecutionType;
import com.haizhi.graph.dc.core.constant.OperateType;
import com.haizhi.graph.dc.core.constant.TaskState;
import com.haizhi.graph.dc.core.constant.TaskType;
import com.haizhi.graph.dc.core.model.po.DcTaskPo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by chengangxiong on 2019/01/31
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "任务显示对象DcTaskVo", description = "用于展示任务信息")
public class DcTaskPageVo extends BaseVo {

    private static final GLog LOG = LogFactory.getLogger(DcTaskPageVo.class);

    @ApiModelProperty(value = "任务名称", example = "task_name")
    private String taskName;

    @ApiModelProperty(value = "任务状态", example = "RUNNING")
    private TaskState taskState;

    @ApiModelProperty(value = "任务类型", example = "HDFS")
    private TaskType taskType;

    @ApiModelProperty(value = "操作类型", example = "UPSERT")
    private OperateType operateType;

    @ApiModelProperty(value = "任务大小", example = "1024")
    private Integer totalSize;

    @ApiModelProperty(value = "总行数", example = "891")
    private Integer totalRows;

    @ApiModelProperty(value = "执行方式", example = "CRON")
    private ExecutionType executionType;

    @ApiModelProperty(value = "最后执行时间", example = "2018-12-24 14:31:27")
    private String lastExecuteDt;

    @ApiModelProperty(value = "cron表达式", example = "0/10 * * * * ? ")
    private String cron;

    @ApiModelProperty(value = "任务状态", example = "RUNNING")
    private String taskDisplayState;

    @ApiModelProperty(value = "错误处理方式，-1表示出错继续，大于或等于0大于该值终止", example = "-1")
    private Integer errorMode;

    public DcTaskPageVo(DcTaskPo dcTaskPo) {
        super(dcTaskPo);
        taskName = dcTaskPo.getTaskName();
        taskType = dcTaskPo.getTaskType();
        executionType = dcTaskPo.getExecutionType();
        taskState = dcTaskPo.getTaskState();
        cron = dcTaskPo.getCron();
    }
}
