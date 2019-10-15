package com.haizhi.graph.sys.auth.model.suo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Data
@ApiModel(value = "用户新增或更新参数SysUserSuo",description = "用于用户新增或更新")
public class SysUserSuo {

    @ApiModelProperty(value = "用户ID，如果为NULL表示新增", example = "13")
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名(表示员工编号、登录名)", example = "test")
    private String userNo;

    @ApiModelProperty(value = "用户密码", example = "123456")
    private String password;

    @ApiModelProperty(value = "姓名",example = "张三")
    private String name;

    @ApiModelProperty(value = "手机或电话", example = "13811112222")
    private String phone;

    @ApiModelProperty(value = "电子邮箱", example = "abc@gmail.com")
    private String email;

    @NotEmpty(message = "系统角色不能为空")
    @ApiModelProperty(value = "系统角色id", example = "[1,2]")
    private Set<Long> roleIds = new HashSet<>();

}
