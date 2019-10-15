package com.haizhi.graph.dc.core.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengangxiong on 2019/04/25
 */
@Data
@ApiModel(value = "服务器路径校验对象DcTaskServerPathCheckQo", description = "")
public class DcTaskServerPathCheckQo {

    @ApiModelProperty(value = "服务器路径", required = true, example = "/tmp")
    private String serverPath;
}
