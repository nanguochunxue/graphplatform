package com.haizhi.graph.dc.core.model.vo;

import com.haizhi.graph.common.constant.Constants;
import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.model.BaseVo;
import com.haizhi.graph.dc.core.model.po.DcSchemaPo;
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
@ApiModel(value = "表信息查询对象DcSchemaVo", description = "资源库管理")
public class DcSchemaVo extends BaseVo {

    @ApiModelProperty(value = "表名称", example = "schema_name")
    private String schema;

    @ApiModelProperty(value = "表中文名称", example = "测试表名")
    private String schemaNameCn;

    @ApiModelProperty(value = "表类型", example = "EDGE")
    private SchemaType type;

    @ApiModelProperty(value = "搜索权重", example = "3")
    private int searchWeight;

    @ApiModelProperty(value = "备注", example = "16")
    private String remark;

    @ApiModelProperty(value = "是否导入hbase", example = "true")
    private boolean useHBase;

    @ApiModelProperty(value = "是否导入图数据库", example = "true")
    private boolean useGdb;

    @ApiModelProperty(value = "是否导入es", example = "true")
    private boolean useSearch;

    @ApiModelProperty(value = "是否允许修改", example = "true")
    private boolean modifiable;

    @ApiModelProperty(value = "是否有方向", example = "false")
    private boolean directed;

    public DcSchemaVo(DcSchemaPo po) {
        super(po);
        this.schema = po.getSchema();
        this.schemaNameCn = po.getSchemaNameCn();
        this.type = po.getType();
        this.searchWeight = po.getSearchWeight();
        this.remark = po.getRemark();
        this.useGdb = po.isUseGdb();
        this.useHBase = po.isUseHBase();
        this.useSearch = po.isUseSearch();
        this.modifiable = po.isModifiable();
        this.directed = Constants.Y.equals(po.getDirected());
    }
}
