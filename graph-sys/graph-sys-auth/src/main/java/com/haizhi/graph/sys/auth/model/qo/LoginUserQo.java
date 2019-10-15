package com.haizhi.graph.sys.auth.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create by zhoumingbing on 2019-05-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "获取登录用户信息 -- SSO")
public class LoginUserQo {

    @ApiModelProperty(name = "登录token", value = "token", example = "45d4ca801d7ec33071574db67fde68a1")
    private String token;

    @ApiModelProperty(name = "是否刷新登录时间", value = "freshExpireTime", example = "false")
    private boolean freshExpireTime;
}
