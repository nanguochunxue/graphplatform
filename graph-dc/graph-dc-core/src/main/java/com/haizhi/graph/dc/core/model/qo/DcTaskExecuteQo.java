package com.haizhi.graph.dc.core.model.qo;

import com.haizhi.graph.dc.core.constant.OperateType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by chengangxiong on 2019/02/15
 */
@Data
@ApiModel(value = "任务执行参数DcTaskExecuteQo", description = "任务执行")
@NoArgsConstructor
public class DcTaskExecuteQo {

    @ApiModelProperty(value = "任务id", required = true)
    private Long taskId;

    @ApiModelProperty(value = "任务操作类型", required = true)
    private OperateType operateType;
}
