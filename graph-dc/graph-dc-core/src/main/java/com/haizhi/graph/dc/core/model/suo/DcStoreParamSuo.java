package com.haizhi.graph.dc.core.model.suo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengangxiong on 2019/05/05
 */
@Data
@ApiModel(value = "存储参数保存对象DcStoreParamSuo", description = "")
public class DcStoreParamSuo {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "key值", example = "key_test")
    private String key;

    @ApiModelProperty(value = "value值", example = "value_test")
    private String value;
}
