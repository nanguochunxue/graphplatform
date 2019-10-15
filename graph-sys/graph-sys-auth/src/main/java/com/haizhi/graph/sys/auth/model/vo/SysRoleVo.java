package com.haizhi.graph.sys.auth.model.vo;

import com.haizhi.graph.sys.auth.model.po.SysRolePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "角色基础信息类SysRoleVo", description = "用于展示角色基础信息")
public class SysRoleVo {

    @ApiModelProperty(value = "角色ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "角色名", example = "admin")
    private String name;

    @ApiModelProperty(value = "角色code", example = "123")
    private String code;

    @ApiModelProperty(value = "角色备注", example = "123")
    private String remark;

    public SysRoleVo(SysRolePo po) {
        this.id = po.getId();
        this.name = po.getName();
        this.code = po.getCode();
        this.remark = po.getRemark();
    }

}
