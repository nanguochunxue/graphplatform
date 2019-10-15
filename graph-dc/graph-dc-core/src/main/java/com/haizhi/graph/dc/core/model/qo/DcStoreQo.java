package com.haizhi.graph.dc.core.model.qo;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.model.PageQoBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by chengangxiong on 2019/01/08
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "数据源对象DcStoreQo", description = "搜索条件")
public class DcStoreQo extends PageQoBase {

    @ApiModelProperty(value = "数据源名称", example = "store_hbase")
    private String name;

    @ApiModelProperty(value = "数据源类型", example = "Hbase")
    private StoreType storeType;
}
