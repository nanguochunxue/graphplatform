package com.haizhi.graph.dc.core.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengangxiong on 2019/05/05
 */
@Data
@ApiModel(value = "GP卸数服务连通性测试对象GPExporterConnectQo", description = "连通性测试")
public class GPExporterConnectQo {

    @ApiModelProperty(value = "卸数服务url", example = "", required = true)
    private String exporterUrl;
}
