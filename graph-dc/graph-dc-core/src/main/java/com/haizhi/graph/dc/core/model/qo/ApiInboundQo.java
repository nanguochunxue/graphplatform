package com.haizhi.graph.dc.core.model.qo;

import com.haizhi.graph.common.model.PageQoBase;
import com.haizhi.graph.dc.core.constant.TaskType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by chengangxiong on 2019/02/22
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "api接入查询条件ApiInboundQo", description = "")
@NoArgsConstructor
public class ApiInboundQo extends PageQoBase {

    @ApiModelProperty(value = "数据源地址id", example = "12")
    private Long storeId;

    @ApiModelProperty(value = "接入时间", example = "2019-02-27")
    private String inboundDt;

    @ApiModelProperty(value = "错误记录", example = "all")
    private String errorRecord;

    private static final TaskType taskType = TaskType.API;
}
