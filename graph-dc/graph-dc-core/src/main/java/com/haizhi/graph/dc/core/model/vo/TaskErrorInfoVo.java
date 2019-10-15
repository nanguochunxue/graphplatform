package com.haizhi.graph.dc.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create by zhoumingbing on 2019-05-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "错误记录查看结果", description = "用于查看错误原因")
public class TaskErrorInfoVo {

    @ApiModelProperty(value = "数据行", example = "")
    private String dataRow;

    @ApiModelProperty(value = "日志", example = "")
    private String errorLog;

}
