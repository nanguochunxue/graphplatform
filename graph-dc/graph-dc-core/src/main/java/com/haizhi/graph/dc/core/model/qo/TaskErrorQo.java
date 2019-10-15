package com.haizhi.graph.dc.core.model.qo;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.model.PageQoBase;
import com.haizhi.graph.dc.core.constant.ErrorType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by chengangxiong on 2019/04/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@ApiModel(value = "错误详情table查询TaskErrorQo", description = "")
@NoArgsConstructor
public class TaskErrorQo extends PageQoBase {

    @ApiModelProperty(value = "任务实例ID", example = "2000000", required = true)
    private Long taskInstanceId;

    @ApiModelProperty(value = "错误类型", example = "RUNTIME_ERROR")
    private ErrorType errorType;

    @ApiModelProperty(value = "受影响的库", example = "ES")
    private StoreType storeType;

    @ApiModelProperty(value = "数据库名", example = "crm_dev2")
    private String graph;

    @ApiModelProperty(value = "表名", example = "test_type1")
    private String schemaName;
}
