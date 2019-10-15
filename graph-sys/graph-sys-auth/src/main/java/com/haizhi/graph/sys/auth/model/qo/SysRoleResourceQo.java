package com.haizhi.graph.sys.auth.model.qo;

import com.haizhi.graph.sys.auth.constant.AuthGroup;
import com.haizhi.graph.sys.auth.constant.SysType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Data
@ToString(callSuper = true)
@ApiModel(value = "角色功能权限查询参数",description = "用于表示查询角色功能权限查询参数")
public class SysRoleResourceQo {

    @NotNull(message = "角色Id不能为空")
    @ApiModelProperty(value = "角色id", example = "2" )
    private Long roleId;

    @NotNull(message = "系统类型不能为空")
    @ApiModelProperty(value = "系统类型", example = "GAP" )
    private SysType type;

    @ApiModelProperty(value = "权限分组", example = "SERVICE" )
    private AuthGroup group;

    @ApiModelProperty(value = "是否有权限", example = "true" )
    private Boolean checked;
}
