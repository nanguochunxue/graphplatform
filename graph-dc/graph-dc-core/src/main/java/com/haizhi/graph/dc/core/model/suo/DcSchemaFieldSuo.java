package com.haizhi.graph.dc.core.model.suo;

import com.haizhi.graph.common.constant.FieldType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengmo on 2018/10/17.
 */
@Data
@ApiModel(value = "表字段查询对象DcGraphSchemaSuo", description = "")
public class DcSchemaFieldSuo {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "库名", example = "graph_one", required = true)
    private String graph;

    @ApiModelProperty(value = "表名", example = "schema_one", required = true)
    private String schema;

    @ApiModelProperty(value = "字段名", example = "country", required = true)
    private String field;

    @ApiModelProperty(value = "字段中文名", example = "中文字段名", required = true)
    private String fieldNameCn;

    @ApiModelProperty(value = "字段类型", example = "STRING", required = true)
    private FieldType type;

    @ApiModelProperty(value = "是否主字段", example = "false", required = true)
    private boolean isMain;

    @ApiModelProperty(value = "搜索权重", example = "2")
    private int searchWeight;
}
