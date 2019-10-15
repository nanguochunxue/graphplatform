package com.haizhi.graph.dc.core.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by chengangxiong on 2019/02/18
 */
@Data
@ApiModel(value = "任务进度查询BatchTaskProcessQo", description = "任务分页查询-任务进度查询")
@NoArgsConstructor
public class BatchTaskProcessQo {

    @ApiModelProperty(value = "ids")
    private List<Long> ids;
}
