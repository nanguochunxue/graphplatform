package com.haizhi.graph.sys.auth.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by liulu on 2019/4/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "存在性检查的返回对象",description = "用于表示存在性检查的返回对象")
public class CheckExistVo {

    @ApiModelProperty(value = "是否存在,true:存在，false:不存在", example = "true")
    private boolean exist;
}
