package com.haizhi.graph.dc.core.model.qo;

import com.haizhi.graph.common.model.PageQoBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

/**
 * Created by chengangxiong on 2019/01/08
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "顶点和边查询对象DcVertexEdgeQo", description = "搜索条件")
public class DcVertexEdgeQo extends PageQoBase {

    @ApiModelProperty(value = "边名称", example = "element")
    private String edge;

    @ApiModelProperty(value = "图名称", example = "graph_one")
    private String graph;

    @ApiModelProperty(value = "边表列表，用于图查询，根据边表查询关联顶点表", example = "[\"test1\",\"test2\"]")
    private Set<String> edges;

}
