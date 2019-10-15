package com.haizhi.graph.common.core.login.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by chengmo on 2018/1/4.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "登录用户的系统角色信息", description = "用于表示登录用户的系统角色信息")
public class RoleVo implements Serializable {
    private static final long serialVersionUID = 2L;

    @ApiModelProperty(value = "角色Id", example = "1")
    protected Long id;

    @ApiModelProperty(value = "角色名称", example = "管理员")
    protected String name;

    @ApiModelProperty(value = "更新时间", example = "2019-06-28 12:30:31")
    protected Date updateTime;

}
