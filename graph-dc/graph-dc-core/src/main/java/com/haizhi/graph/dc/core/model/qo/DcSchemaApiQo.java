package com.haizhi.graph.dc.core.model.qo;

import com.haizhi.graph.common.constant.SchemaType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by chengangxiong on 2019/04/09
 */
@Data
@ApiModel(value = "表查询对象DcSchemaApiQo", description = "搜索条件")
public class DcSchemaApiQo {

    @ApiModelProperty(value = "表名称搜索关键词", example = "schema_name/中文关键词")
    private String search;

    @ApiModelProperty(value = "表类型", example = "EDGE")
    private SchemaType type;

    @ApiModelProperty(value = "资源库Id", example = "3", required = true)
    private Long graphId;
}
