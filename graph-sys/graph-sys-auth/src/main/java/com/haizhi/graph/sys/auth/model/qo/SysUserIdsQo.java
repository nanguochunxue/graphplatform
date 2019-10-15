package com.haizhi.graph.sys.auth.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * Created by liulu on 2019/6/18.
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "用户查询参数SysUserIdsQo", description = "用于查询用户信息,GAP系统专用")
public class SysUserIdsQo {

    @ApiModelProperty(value = "用户名(表示员工id列表)", example = "[1,3]")
    private List<Long> userIdList;
}
