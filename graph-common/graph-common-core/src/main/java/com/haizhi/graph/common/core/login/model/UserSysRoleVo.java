package com.haizhi.graph.common.core.login.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by chengmo on 2018/1/4.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "登录用户及其系统角色信息", description = "用于表示登录用户相关信息")
public class UserSysRoleVo implements Serializable {
    private static final long serialVersionUID = 2L;

    @ApiModelProperty(value = "用户Id", example = "1")
    protected Long id;

    @ApiModelProperty(value = "用户名", example = "000001")
    protected String userNo;

    @ApiModelProperty(value = "用户名称", example = "张三")
    protected String name;

    @ApiModelProperty(value = "用户密码", example = "123456")
    protected String password;

    @ApiModelProperty(value = "更新时间", example = "2019-06-28 12:30:31")
    protected Date updateTime;

    @ApiModelProperty(value = "系统角色列表")
    protected List<RoleVo> roles;

}
