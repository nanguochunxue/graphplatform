package com.haizhi.graph.dc.core.model.qo;

import com.haizhi.graph.common.constant.StoreType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengangxiong on 2019/03/25
 */
@Data
@ApiModel(value = "自动获取url查询对象DcStoreAutoUrlQo", description = "")
public class DcStoreAutoUrlQo {

    @ApiModelProperty(value = "环境ID", example = "1", required = true)
    private Long envId;

    @ApiModelProperty(value = "数据源类型", example = "HDFS", required = true)
    private StoreType storeType;
}
