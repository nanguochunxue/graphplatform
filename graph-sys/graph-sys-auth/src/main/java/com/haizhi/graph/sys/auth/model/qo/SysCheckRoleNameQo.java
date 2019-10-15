package com.haizhi.graph.sys.auth.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by liulu on 2019/4/16.
 */
@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "检查系统角色名重复性的请求对象",description = "用于表示检查角色名称重复性的请求对象")
public class SysCheckRoleNameQo {

    @ApiModelProperty(value = "系统角色ID，为NULL时代表添加角色时检查", example = "1")
    private Long roleId;

    @NotBlank(message = "角色名不能为空")
    @ApiModelProperty(value = "角色名,全局唯一",example = "项目管理员")
    private String roleName;
}
