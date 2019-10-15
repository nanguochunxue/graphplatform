package com.haizhi.graph.sys.auth.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by liulu on 2019/6/17.
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "用户查询参数SysUserQo", description = "用于查询用户信息,GAP系统专用")
public class SysUserQo{

    @ApiModelProperty(value = "用户名(表示员工编号、登录名)，如果与name同时赋值，两个条件是or的关系", example = "000001")
    private String userNo;

    @ApiModelProperty(value = "姓名，如果与用户名userNo同时赋值，两个条件是or的关系", example = "张三")
    private String name;
}
