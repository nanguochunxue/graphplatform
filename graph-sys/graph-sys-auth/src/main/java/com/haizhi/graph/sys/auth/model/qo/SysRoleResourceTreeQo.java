package com.haizhi.graph.sys.auth.model.qo;

import com.haizhi.graph.sys.auth.constant.SysType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by liulu on 2019/6/18.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@ApiModel(value = "获取系统角色权限树请求对象",description = "用于表示获取系统角色权限树请求对象")
public class SysRoleResourceTreeQo {

    @ApiModelProperty(value = "系统角色id", example = "4" )
    private Long roleId;

    @ApiModelProperty(value = "系统类型", example = "SYS" )
    private SysType sysType;
}
