package com.haizhi.graph.dc.core.model.qo;

import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.model.PageQoBase;
import com.haizhi.graph.dc.core.constant.QueryType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by chengmo on 2018/10/17.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "表查询对象DcSchemaQo", description = "搜索条件")
public class DcSchemaQo extends PageQoBase {

    @ApiModelProperty(value = "表名称搜索关键词", example = "schema_name/中文关键词")
    private String search;

    @ApiModelProperty(value = "搜索关键字类型 ALL:所有，EN:英文关键字，CH:中文关机字", example = "ALL")
    private QueryType queryType;

    @ApiModelProperty(value = "资源库名称", example = "graph_one", required = true)
    private String graph;

    @ApiModelProperty(value = "表类型", example = "EDGE")
    private SchemaType type;
}
