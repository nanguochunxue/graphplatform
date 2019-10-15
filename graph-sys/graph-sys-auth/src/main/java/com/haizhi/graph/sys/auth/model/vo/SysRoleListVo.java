package com.haizhi.graph.sys.auth.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liulu on 2019/6/18.
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "角色列表信息类", description = "用于展示角色基础信息")
public class SysRoleListVo {

    @ApiModelProperty(value = "预定义角色列表")
    private List<SysRoleVo> predefineRoles = new ArrayList<>();

    @ApiModelProperty(value = "自定义角色列表")
    private List<SysRoleVo> customizeRoles = new ArrayList<>();

}
