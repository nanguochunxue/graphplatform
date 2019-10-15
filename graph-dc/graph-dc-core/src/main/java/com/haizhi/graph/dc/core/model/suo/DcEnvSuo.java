package com.haizhi.graph.dc.core.model.suo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengangxiong on 2019/03/25
 */
@Data
@ApiModel(value = "数据源编辑对象DcEnvSuo", description = "")
public class DcEnvSuo {

    @ApiModelProperty(value = "主键id", example = "16")
    private Long id;

    @ApiModelProperty(value = "环境名称", example = "name", required = true)
    private String name;

    @ApiModelProperty(value = "版本", example = "3", required = true)
    private Long versionDictId;

    @ApiModelProperty(value = "认证用户", example = "hadoop")
    private String user;

    @ApiModelProperty(value = "备注", example = "备注txt")
    private String remark;

    @ApiModelProperty(value = "文件ID")
    private Long[] fileIds;

    @ApiModelProperty(value = "是否启用安全验证", example = "false")
    private boolean securityEnabled;
}
