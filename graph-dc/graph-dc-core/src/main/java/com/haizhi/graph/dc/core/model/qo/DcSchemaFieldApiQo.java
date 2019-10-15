package com.haizhi.graph.dc.core.model.qo;

import com.haizhi.graph.common.constant.FieldType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengangxiong on 2019/04/12
 */
@Data
@ApiModel(value = "表字段查询对象DcSchemaFieldApiQo-api", description = "表字段查询-api")
public class DcSchemaFieldApiQo {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "表名ID", example = "1", required = true)
    private Long graphId;

    @ApiModelProperty(value = "表名ID", example = "2", required = true)
    private Long schemaId;

    @ApiModelProperty(value = "字段名搜索关键词", example = "country/中文关键词")
    private String search;

    @ApiModelProperty(value = "字段类型", example = "STRING")
    private FieldType type;
}
