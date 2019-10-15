package com.haizhi.graph.dc.core.model.vo;

import com.haizhi.graph.common.model.BaseVo;
import com.haizhi.graph.dc.core.model.po.DcVertexEdgePo;
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
@ApiModel(value = "顶点和边信息查询对象DcStoreVo", description = "资源库管理")
public class DcVertexEdgeVo extends BaseVo {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "边名称", example = "element")
    private String edge;

    @ApiModelProperty(value = "图名称", example = "graph_one")
    private String graph;

    @ApiModelProperty(value = "from表名称", example = "from_schema")
    private String fromSchema;

    @ApiModelProperty(value = "to表名称", example = "to_schema")
    private String toSchema;

    public DcVertexEdgeVo(DcVertexEdgePo po) {
        this.id = po.getId();
        this.edge = po.getEdge();
        this.graph = po.getGraph();
        this.fromSchema = po.getFromVertex();
        this.toSchema = po.getFromVertex();
    }
}
