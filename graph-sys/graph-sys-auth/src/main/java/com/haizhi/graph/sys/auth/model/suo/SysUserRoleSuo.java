package com.haizhi.graph.sys.auth.model.suo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Data
@ApiModel(value = "用户授权SysUserRoleSuo",description = "用户授权")
public class SysUserRoleSuo {

    @ApiModelProperty(value = "主键ID，如果为NULL表示新增", example = "3")
    private Long id;

    @Valid
    @ApiModelProperty(value = "用户ID", example = "11")
    private String userNo;

    @ApiModelProperty(value = "角色ID", example = "22")
    private String roleId;

}
