package com.haizhi.graph.dc.core.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengangxiong on 2019/03/11
 */
@Data
@ApiModel(value = "元数据-表名称校验对象DcSchemaNameCheckQO", description = "校验参数")
public class DcSchemaCheckQO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "库名称", example = "test", required = true)
    private String graph;

    @ApiModelProperty(value = "表名称", example = "test", required = true)
    private String schema;
}
