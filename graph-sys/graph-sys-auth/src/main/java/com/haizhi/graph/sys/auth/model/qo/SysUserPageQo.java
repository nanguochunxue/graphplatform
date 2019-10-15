package com.haizhi.graph.sys.auth.model.qo;

import com.haizhi.graph.common.model.PageQoBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "用户查询参数SysUserPageQo", description = "用于分页查询")
public class SysUserPageQo extends PageQoBase {

    @ApiModelProperty(value = "姓名，如果与用户名userNo同时赋值，两个条件是or的关系", example = "张三")
    private String name;

    @ApiModelProperty(value = "用户名(表示员工编号、登录名)，如果与name同时赋值，两个条件是or的关系", example = "000001")
    private String userNo;

    @ApiModelProperty(value = "用户来源:[SSO-单点登录,SYS_CREATION-系统创建]", example = "SSO")
    private String userSource;

    @ApiModelProperty(value = "用户角色ID", example = "1")
    private Long roleId;
}
