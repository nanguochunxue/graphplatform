package com.haizhi.graph.dc.core.model.suo;

import com.haizhi.graph.common.constant.SchemaType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengmo on 2018/10/17.
 */
@Data
@ApiModel(value = "表查询对象DcGraphSchemaSuo", description = "")
public class DcSchemaSuo {

    @ApiModelProperty(value = "主键id", example = "16")
    private Long id;

    @ApiModelProperty(value = "库名称", example = "graph_name", required = true)
    private String graph;

    @ApiModelProperty(value = "表名称", example = "schema_name", required = true)
    private String schema;

    @ApiModelProperty(value = "表中文名称", example = "测试表名", required = true)
    private String schemaNameCn;

    @ApiModelProperty(value = "表类型", example = "EDGE", required = true)
    private SchemaType type;

    @ApiModelProperty(value = "搜索权重", example = "3")
    private int searchWeight;

    @ApiModelProperty(value = "备注", example = "16")
    private String remark;

    @ApiModelProperty(value = "是否导入gdb", example = "false")
    private boolean useGdb = false;

    @ApiModelProperty(value = "是否导入es", example = "false")
    private boolean useSearch = false;

    @ApiModelProperty(value = "是否导入hbase", example = "true")
    private boolean useHBase = false;

    @ApiModelProperty(value = "顺序号", example = "2")
    private int sequence;

    @ApiModelProperty(value = "是否有方向", example = "false")
    private boolean directed;
}
