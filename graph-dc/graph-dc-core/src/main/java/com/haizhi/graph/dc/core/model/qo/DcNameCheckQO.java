package com.haizhi.graph.dc.core.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengangxiong on 2019/03/11
 */
@Data
@ApiModel(value = "元数据-名称校验对象DcNameCheckQO", description = "校验参数")
public class DcNameCheckQO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "名称", example = "test", required = true)
    private String name;
}
