package com.haizhi.graph.dc.core.model.qo;

import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.model.PageQoBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by chengangxiong on 2019/04/15
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "元数据-获取授权字段DcAuthorizedFieldQo", description = "获取授权字段-api")
public class DcAuthorizedFieldQo extends PageQoBase {

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
