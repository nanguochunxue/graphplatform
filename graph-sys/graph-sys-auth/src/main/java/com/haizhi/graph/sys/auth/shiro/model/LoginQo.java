package com.haizhi.graph.sys.auth.shiro.model;

import com.haizhi.graph.common.constant.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * Created by chengmo on 2018/1/4.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "登录请求对象", description = "用于表示登录请求对象")
public class LoginQo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "登录用户名不能为空")
    @ApiModelProperty(value = "登录用户名", example = "000001")
    private String userNo;

    @NotBlank(message = "登录密码不能为空")
    @ApiModelProperty(value = "登录密码", example = "*******")
    private String password;

    @ApiModelProperty(value = "是否自动登录(Y/N),默认为N", example = "N")
    private String autoLogin = Constants.N;
}
