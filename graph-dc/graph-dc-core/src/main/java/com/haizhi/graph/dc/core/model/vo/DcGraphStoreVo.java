package com.haizhi.graph.dc.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Create by zhoumingbing on 2019-06-28
 */
@Data
@ApiModel(value = "库和资源显示信息", description = "库和资源显示信息")
public class DcGraphStoreVo {

    @ApiModelProperty(value = "hbase能选择空", example = "true", required = true)
    private boolean hbaseCanNull = true;

    @ApiModelProperty(value = "es能选择空", example = "true", required = true)
    private boolean esCanNull = true;

    @ApiModelProperty(value = "gdb能选择空", example = "true", required = true)
    private boolean gdbCanNull = true;

}
