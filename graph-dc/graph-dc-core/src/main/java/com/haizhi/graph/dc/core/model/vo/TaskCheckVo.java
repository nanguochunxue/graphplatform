package com.haizhi.graph.dc.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Create by zhoumingbing on 2019-06-29
 */
@Data
@ApiModel(value = "确认结果页面对象")
@AllArgsConstructor
public class TaskCheckVo {

    @ApiModelProperty(value = "确认结果", example = "true")
    private boolean result;

}
