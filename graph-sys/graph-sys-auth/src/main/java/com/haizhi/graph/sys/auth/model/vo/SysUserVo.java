package com.haizhi.graph.sys.auth.model.vo;

import com.haizhi.graph.sys.auth.model.po.SysRolePo;
import com.haizhi.graph.sys.auth.model.po.SysUserPo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "用户信息类SysUserVo", description = "用于展示用户信息")
public class SysUserVo {

    @ApiModelProperty(value = "用户ID", example = "1")
    private long id;

    @ApiModelProperty(value = "用户名", example = "admin")
    private String userNo;

    @ApiModelProperty(value = "姓名(别名)", example = "管理员")
    private String name;

    @ApiModelProperty(value = "手机或电话", example = "13800138000")
    private String phone;

    @ApiModelProperty(value = "电子邮箱", example = "xx@139.com")
    private String email;

    @ApiModelProperty(value = "用户来源:[SSO-单点登录,SYS_CREATION-系统创建]", example = "SSO")
    private String userSource;

    @ApiModelProperty(value = "状态，0-冻结, 1-正常", example = "1")
    private Integer status;

    @ApiModelProperty(value = "系统角色")
    private List<SysRoleVo> roles = new ArrayList<>();

    public SysUserVo(SysUserPo po) {
        this.id = po.getId();
        this.userNo = po.getUserNo();
        this.name = po.getName();
        this.phone = po.getPhone();
        this.email = po.getEmail();
        this.status = po.getStatus();
        this.userSource = po.getUserSource();
    }

    public SysUserVo(SysUserPo userPo, List<SysRolePo> rolePos) {
        this(userPo);
        if (!CollectionUtils.isEmpty(rolePos)){
            this.roles = rolePos.stream().map(rolePo -> new SysRoleVo(rolePo)).collect(Collectors.toList());
        }
    }
}
