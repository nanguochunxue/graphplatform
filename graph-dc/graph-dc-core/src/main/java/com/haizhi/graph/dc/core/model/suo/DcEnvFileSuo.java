package com.haizhi.graph.dc.core.model.suo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengangxiong on 2019/03/25
 */
@Data
@ApiModel(value = "数据源文件编辑对象DcEnvSuo", description = "")
public class DcEnvFileSuo {

    @ApiModelProperty(value = "主键id", example = "16")
    private Long id;

}
