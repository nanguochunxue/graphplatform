package com.haizhi.graph.dc.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengangxiong on 2019/03/11
 */
@Data
@ApiModel(value = "名称校验结果对象DcNameCheckVo", description = "名称校验")
public class DcNameCheckVo {

    @ApiModelProperty(value = "校验结果")
    private boolean checkResult = true;

    @ApiModelProperty(value = "校验信息")
    private String checkMsg;
}
