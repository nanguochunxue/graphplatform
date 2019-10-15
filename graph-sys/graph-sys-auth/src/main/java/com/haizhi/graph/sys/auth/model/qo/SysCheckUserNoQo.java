package com.haizhi.graph.sys.auth.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by liulu on 2019/4/16.
 */
@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "检查用户名重复性的请求对象",description = "用于表示检查用户名重复性的请求对象")
public class SysCheckUserNoQo {

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名,全局唯一",example = "000001")
    private String userNo;
}
