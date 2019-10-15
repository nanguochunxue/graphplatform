package com.haizhi.graph.dc.core.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by chengangxiong on 2019/04/23
 */
@Data
@ApiModel(value = "任务目标字段查询对象TaskDstFieldQo", description = "")
@NoArgsConstructor
public class TaskDstFieldQo {

    @ApiModelProperty(value = "资源库", example = "graph_hello", required = true)
    private String graph;

    @ApiModelProperty(value = "表", example = "schema_hello", required = true)
    private String schema;
}
