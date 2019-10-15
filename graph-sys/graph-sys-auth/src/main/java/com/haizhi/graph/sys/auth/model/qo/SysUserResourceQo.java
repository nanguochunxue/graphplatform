package com.haizhi.graph.sys.auth.model.qo;

import com.haizhi.graph.sys.auth.constant.AuthGroup;
import com.haizhi.graph.sys.auth.constant.SysType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * Created by liulu on 2019/7/4.
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "用户功能权限查询参数", description = "用于表示用户功能权限查询参数,GAP系统专用")
public class SysUserResourceQo {

    @NotNull(message = "用户Id不能为空")
    @ApiModelProperty(value = "用户Id", example = "1")
    private Long userId;

    @ApiModelProperty(value = "系统类型", example = "GAP")
    private SysType type;

    @ApiModelProperty(value = "权限分组类型", example = "S")
    private AuthGroup group;
}
