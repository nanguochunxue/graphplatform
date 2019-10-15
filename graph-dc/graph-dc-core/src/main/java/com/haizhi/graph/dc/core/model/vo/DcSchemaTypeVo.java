package com.haizhi.graph.dc.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by chengangxiong on 2019/02/25
 */
@Data
@NoArgsConstructor
@ApiModel(value = "表类型下拉显示DcSchemaTypeVo", description = "表类型list")
@AllArgsConstructor
public class DcSchemaTypeVo {

    @ApiModelProperty(value = "表类型", example = "VERTEX")
    private List<String> schemaTypeList;

}
