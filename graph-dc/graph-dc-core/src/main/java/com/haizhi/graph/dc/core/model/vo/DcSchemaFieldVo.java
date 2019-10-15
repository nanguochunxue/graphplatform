package com.haizhi.graph.dc.core.model.vo;

import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.model.BaseVo;
import com.haizhi.graph.dc.core.model.po.DcSchemaFieldPo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by chengmo on 2018/8/16.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ApiModel(value = "表字段查询对象DcSchemaFieldVo", description = "服务搜索")
public class DcSchemaFieldVo extends BaseVo {

    @ApiModelProperty(value = "表名", example = "schema_one")
    private String schema;

    @ApiModelProperty(value = "字段名", example = "country")
    private String field;

    @ApiModelProperty(value = "字段中文名", example = "中文字段名")
    private String fieldNameCn;

    @ApiModelProperty(value = "字段类型", example = "STRING")
    private FieldType type;

    @ApiModelProperty(value = "搜索权重", example = "3")
    private int searchWeight;

    @ApiModelProperty(value = "是否为主字段", example = "true")
    private boolean isMain = false;

    @ApiModelProperty(value = "是否允许修改", example = "true")
    private boolean modifiable;

    public DcSchemaFieldVo(DcSchemaFieldPo po) {
        super(po);
        this.schema = po.getSchema();
        this.field = po.getField();
        this.fieldNameCn = po.getFieldNameCn();
        this.type = po.getType();
        this.searchWeight = po.getSearchWeight();
        this.isMain = po.isMain();
        this.modifiable = po.isModifiable();
    }
}