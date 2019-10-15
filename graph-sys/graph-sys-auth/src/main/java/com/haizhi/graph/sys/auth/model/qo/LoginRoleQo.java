package com.haizhi.graph.sys.auth.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create by zhoumingbing on 2019-05-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "登录角色 -- SSO")
public class LoginRoleQo {

    @ApiModelProperty(value = "登录token", example = "61343347d654cc644be3aed4e633936b", required = true)
    private String token;

    @ApiModelProperty(value = "登录角色ID", example = "1", required = true)
    private Long roleId;

}
