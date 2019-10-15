package com.haizhi.graph.dc.core.model.vo;

import com.haizhi.graph.common.model.BaseVo;
import com.haizhi.graph.dc.core.constant.ErrorType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by chengangxiong on 2019/04/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "错误详情table显示TaskErrorVo", description = "")
public class TaskErrorPageVo extends BaseVo {

    @ApiModelProperty(value = "错误类型", example = "RUNTIME_ERROR")
    private ErrorType errorType;

    @ApiModelProperty(value = "数据库")
    private String storeTypes;

    @ApiModelProperty(value = "错误ID")
    private String errorId;

}
