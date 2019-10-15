package com.haizhi.graph.dc.core.model.vo;

import com.haizhi.graph.common.model.BaseVo;
import com.haizhi.graph.dc.core.model.po.DcEnvFilePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by chengangxiong on 2019/03/25
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "环境对象DcEnvVo", description = "环境管理")
public class DcEnvFileVo extends BaseVo {

    @ApiModelProperty(value = "表名称", example = "schema_name")
    private String name;

    public DcEnvFileVo(DcEnvFilePo po) {
        super(po);
        this.name = po.getName();
    }
}
