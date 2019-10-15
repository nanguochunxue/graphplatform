package com.haizhi.graph.dc.core.model.vo;

import com.haizhi.graph.common.model.BaseVo;
import com.haizhi.graph.dc.core.model.po.DcEnvPo;
import com.haizhi.graph.sys.file.model.po.SysDictPo;
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
public class DcEnvVo extends BaseVo {

    @ApiModelProperty(value = "表名称", example = "schema_name")
    private String name;

    @ApiModelProperty(value = "版本", example = "HDP")
    private SysDictPo version;

    @ApiModelProperty(value = "认证用户", example = "hadoop")
    private String user;

    public DcEnvVo(DcEnvPo po, SysDictPo version) {
        super(po);
        this.name = po.getName();
        this.version = version;
        this.user = po.getUser();
    }
}
