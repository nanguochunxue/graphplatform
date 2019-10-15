package com.haizhi.graph.dc.core.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengangxiong on 2019/04/08
 */
@Data
@ApiModel(value = "元数据-是否存在主字段的校验对象DcSchemaFieldMainCheckQO", description = "校验参数")
public class DcSchemaFieldMainCheckQO {

    @ApiModelProperty(value = "资源库", example = "test", required = true)
    private String graph;

    @ApiModelProperty(value = "表名", example = "test", required = true)
    private String schema;
}
