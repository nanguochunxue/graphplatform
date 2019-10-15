package com.haizhi.graph.dc.core.model.qo;

import com.haizhi.graph.common.model.PageQoBase;
import com.haizhi.graph.dc.core.constant.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by chengangxiong on 2019/01/29
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "任务查询对象DcTaskQo", description = "搜索条件")
public class DcTaskQo extends PageQoBase {

    @ApiModelProperty(value = "资源库名称", example = "28")
    private Long taskId;

    @ApiModelProperty(value = "资源库名称", example = "demo_graph")
    private String graph;

    @ApiModelProperty(value = "schema名称", example = "demo_vertex")
    private String schema;

    @ApiModelProperty(value = "任务名称", example = "test_task")
    private String taskName;

    @ApiModelProperty(value = "任务类型", example = "HDFS")
    private TaskType taskType;

    @ApiModelProperty(value = "执行类型", example = "CRON")
    private ExecutionType executionType;

    @ApiModelProperty(value = "操作类型", example = "UPSERT")
    private OperateType operateType;

    @ApiModelProperty(value = "任务状态", example = "RUNNING")
    private TaskState taskState;

    @ApiModelProperty(value = "导入进度", example = "RUNNING")
    private TaskInstanceState taskInstanceState;
}
