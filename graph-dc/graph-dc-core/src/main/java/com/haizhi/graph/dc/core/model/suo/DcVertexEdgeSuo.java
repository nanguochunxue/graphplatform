package com.haizhi.graph.dc.core.model.suo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengangxiong on 2019/01/08
 */
@Data
@ApiModel(value = "顶点和边查询对象DcGraphSchemaSuo", description = "")
public class DcVertexEdgeSuo {

    @ApiModelProperty(value = "主键id", example = "16")
    private Long id;

    @ApiModelProperty(value = "边名称", example = "element")
    private String edge;

    @ApiModelProperty(value = "图名称", example = "graph_one")
    private String graph;

    @ApiModelProperty(value = "from表id", example = "43")
    private Long fromSchemaId;

    @ApiModelProperty(value = "to表id", example = "45")
    private Long toSchemaId;

    @ApiModelProperty(value = "边id", example = "89")
    private Long edgeId;

    @ApiModelProperty(value = "from表名称", example = "from_schema")
    private String fromSchema;

    @ApiModelProperty(value = "to表名称", example = "to_schema")
    private String toSchema;
}
