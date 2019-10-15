package com.haizhi.graph.dc.core.model.suo;

import com.haizhi.graph.common.constant.StoreType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create by zhoumingbing on 2019-05-13
 */
@Data
@NoArgsConstructor
@ApiModel
public class DcGraphStoreSuo {

    @ApiModelProperty(value = "主键ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "库名称", example = "test2")
    private String graph;

    @ApiModelProperty(value = "dc_store主键ID", example = "1")
    private Long storeId;

    @ApiModelProperty(value = "数据库类型", example = "ES")
    private StoreType storeType;
}
