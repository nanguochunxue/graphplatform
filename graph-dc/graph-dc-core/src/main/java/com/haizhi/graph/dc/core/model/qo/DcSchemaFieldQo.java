package com.haizhi.graph.dc.core.model.qo;

import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.model.PageQoBase;
import com.haizhi.graph.dc.core.constant.QueryType;
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
@ApiModel(value = "表字段查询对象DcSchemaFieldQo", description = "搜索条件")
public class DcSchemaFieldQo extends PageQoBase {

    @ApiModelProperty(value = "库名", example = "graph-one", required = true)
    private String graph;

    @ApiModelProperty(value = "表名", example = "schema_one", required = true)
    private String schema;

    @ApiModelProperty(value = "字段名搜索关键词", example = "country/中文关键词")
    private String search;

    @ApiModelProperty(value = "字段类型", example = "STRING")
    private FieldType type;

    @ApiModelProperty(value = "搜索关键字类型 ALL:所有，EN:英文关键字，CH:中文关机字", example = "ALL")
    private QueryType queryType;
}
