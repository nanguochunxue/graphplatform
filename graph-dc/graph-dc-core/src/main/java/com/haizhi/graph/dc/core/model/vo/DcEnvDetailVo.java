package com.haizhi.graph.dc.core.model.vo;

import com.haizhi.graph.common.constant.Constants;
import com.haizhi.graph.common.model.BaseVo;
import com.haizhi.graph.dc.core.model.po.DcEnvPo;
import com.haizhi.graph.sys.file.model.po.SysDictPo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Created by chengangxiong on 2019/03/25
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "环境对象DcEnvVo", description = "环境管理")
public class DcEnvDetailVo extends BaseVo {

    @ApiModelProperty(value = "表名称", example = "schema_name")
    private String name;

    @ApiModelProperty(value = "版本", example = "HDP")
    private SysDictPo version;

    @ApiModelProperty(value = "认证用户", example = "hadoop")
    private String user;

    @ApiModelProperty(value = "备注", example = "这是个备注")
    private String remark;

    @ApiModelProperty(value = "是否启用安全验证", example = "false")
    private boolean securityEnabled;

    @ApiModelProperty(value = "文件列表", example = "文件列表")
    private List<DcEnvFileVo> fileVoList;

    public DcEnvDetailVo(DcEnvPo po, SysDictPo version, List<DcEnvFileVo> fileVoList) {
        super(po);
        this.name = po.getName();
        this.version = version;
        this.user = po.getUser();
        this.remark = po.getRemark();
        this.fileVoList = fileVoList;
        securityEnabled = po.getSecurityEnabled().equals(Constants.Y);
    }
}
