package com.haizhi.graph.search.api.model.qo;

import com.haizhi.graph.common.model.BaseQo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by tanghaiyang on 2019/7/2.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "代理查询对象ProxyQo", description = "代理查询")
public class ProxyQo extends BaseQo {

    @ApiModelProperty(value = "图数据库", required = true)
    private String graph;

    @ApiModelProperty(value = "代理查询条件，包含方法，uri和查询条件", required = true)
    private String request;

}