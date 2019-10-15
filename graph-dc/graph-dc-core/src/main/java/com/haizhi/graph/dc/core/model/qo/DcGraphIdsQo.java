package com.haizhi.graph.dc.core.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by chengangxiong on 2019/04/04
 */
@Data
@ApiModel(value = "元数据-资源库查询对象DcGraphIdsQo", description = "根据多个ID查询")
public class DcGraphIdsQo {

    @ApiModelProperty(value = "id list", required = true)
    private List<Long> ids;
}
