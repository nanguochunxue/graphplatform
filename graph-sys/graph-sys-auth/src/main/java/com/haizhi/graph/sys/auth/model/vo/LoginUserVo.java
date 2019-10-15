package com.haizhi.graph.sys.auth.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * Create by zhoumingbing on 2019-06-11
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "登录系统用户信息", description = "用于表示登录系统用户信息")
public class LoginUserVo {

    @ApiModelProperty(value = "用户Id", example = "1")
    private Long id;

    @ApiModelProperty(value = "用户编号", example = "000001")
    private String userNo;

    @ApiModelProperty(value = "用户名称", example = "张三")
    private String userName;

    @ApiModelProperty(value = "用户电话", example = "13510659346")
    private String phone;

    @ApiModelProperty(value = "用户邮箱", example = "test@xxx.com")
    private String email;

    @ApiModelProperty(value = "用户状态，取值[1-正常，0-冻结]", example = "1")
    private Integer status;

    @ApiModelProperty(value = "系统角色列表")
    private List<SysRoleVo> sysRoles;

    @ApiModelProperty(value = "登录角色系统在各平台的功能权限资源列表,key为系统平台类型，如SYS、DMP、GAP、AP等")
    private Map<String, List<SysResourceVo>> resourceTrees;
}
