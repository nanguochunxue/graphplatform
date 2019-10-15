package com.haizhi.graph.sys.auth.model.vo;

import com.haizhi.graph.sys.auth.model.po.SysUserRolePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "用户角色信息类SysUserRoleVo", description = "用于展示用户角色信息")
public class SysUserRoleVo {

    @ApiModelProperty(value = "用户ID", example = "1")
    private long id;

    @ApiModelProperty(value = "用户ID", example = "1")
    private Long userId;

    @ApiModelProperty(value = "角色ID", example = "3")
    private Long roleId;

    @ApiModelProperty(value = "角色名称", example = "3")
    private String roleName;

    @ApiModelProperty(value = "角色备注", example = "3")
    private String remark;

}
