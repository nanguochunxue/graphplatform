package com.haizhi.graph.sys.auth.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by liulu on 2019/6/17.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "登录成功返回的结果信息", description = "用于表示登录成功返回的结果信息")
public class LoginVo {

    @ApiModelProperty(value = "登录成功返回的SessionId", example = "6a2eec94-aff9-480c-9781-58c5c3483936")
    private String sessionId;
}
