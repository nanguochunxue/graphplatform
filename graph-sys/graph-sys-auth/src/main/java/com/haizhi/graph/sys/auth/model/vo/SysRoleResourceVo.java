package com.haizhi.graph.sys.auth.model.vo;

import com.haizhi.graph.sys.auth.model.po.SysRolePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "角色单个平台的权限信息", description = "用于展示角色单个平台的权限信息")
public class SysRoleResourceVo {

    @ApiModelProperty(value = "角色ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "角色名", example = "admin")
    private String name;

    @ApiModelProperty(value = "角色code", example = "123")
    private String code;

    @ApiModelProperty(value = "角色备注", example = "123")
    private String remark;

    @ApiModelProperty(value = "单个平台系统的权限树")
    private List<SysResourceVo> resources = new ArrayList<>();

    public SysRoleResourceVo(SysRolePo po) {
        this.id = po.getId();
        this.name = po.getName();
        this.code = po.getCode();
        this.remark = po.getRemark();

    }

}
