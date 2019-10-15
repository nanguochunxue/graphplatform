package com.haizhi.graph.sys.auth.model.uo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by liulu on 2019/6/18.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "用户密码更新请求对象",description = "用于表示用户密码更新请求对象")
public class SysUserPwdUo {

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名(表示员工编号、登录名)", example = "test")
    private String userNo;

    @NotBlank(message = "新密码不能为空")
    @ApiModelProperty(value = "用户新密码", example = "111111")
    private String password;

    @NotBlank(message = "原密码不能为空")
    @ApiModelProperty(value = "用户原密码", example = "123456")
    private String passwordOld;
}
