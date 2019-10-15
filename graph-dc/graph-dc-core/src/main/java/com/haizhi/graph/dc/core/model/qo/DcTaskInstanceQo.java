package com.haizhi.graph.dc.core.model.qo;

import com.haizhi.graph.common.model.PageQoBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by chengangxiong on 2019/02/11
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "任务实例查询对象DcTaskInstanceQo", description = "搜索条件")
public class DcTaskInstanceQo extends PageQoBase {

    @ApiModelProperty(value = "任务id", required = true, example = "12")
    private Long taskId;
}
