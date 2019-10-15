package com.haizhi.graph.sys.auth.model.uo;

import com.haizhi.graph.sys.auth.constant.SysType;
import com.haizhi.graph.sys.auth.model.vo.SysResourceVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "系统角色授权请求对象",description = "用于表示系统角色授权请求对象")
public class SysRoleResourceSuo {

    @NotNull(message = "系统类型不能为空")
    @ApiModelProperty(value = "系统类型", example = "SYS")
    private SysType type;

    @NotNull(message = "角色ID不能为空")
    @ApiModelProperty(value = "角色id", example = "10")
    private Long roleId;

    @ApiModelProperty(value = "功能资源树")
    private List<SysResourceVo> roleResources;

}
