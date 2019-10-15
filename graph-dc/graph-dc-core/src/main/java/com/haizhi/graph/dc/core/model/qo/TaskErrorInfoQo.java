package com.haizhi.graph.dc.core.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create by zhoumingbing on 2019-05-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "查看错误")
public class TaskErrorInfoQo {

    @ApiModelProperty(value = "任务实例ID", example = "2000000", required = true)
    private Long taskInstanceId;

    @ApiModelProperty(value = "数据库名", example = "crm_dev2")
    private String graph;

    @ApiModelProperty(value = "表名", example = "test_type1")
    private String schema;

    @ApiModelProperty(value = "错误ID", example = "100021_ES", required = true)
    private String errorId;

}
