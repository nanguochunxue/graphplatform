package com.haizhi.graph.sys.auth.model.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.haizhi.graph.common.model.BaseTreeNodeVo;
import com.haizhi.graph.sys.auth.model.po.SysResourcePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by tanghaiyang on 2019/1/10.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ApiModel(value = "资源信息类SysResourceVo", description = "用于展示资源信息")
public class SysResourceVo extends BaseTreeNodeVo<SysResourceVo> {

    @JSONField(ordinal = 1)
    @ApiModelProperty(value = "资源名", example = "列表")
    private String name;

    @JSONField(ordinal = 2)
    @ApiModelProperty(value = "资源级别", example = "一级资源")
    private String remark;

    @JSONField(ordinal = 3)
    @ApiModelProperty(value = "资源url", example = "http://xxxx:xx/api/xx")
    private String url;

    @JSONField(ordinal = 4)
    @ApiModelProperty(value = "资源类型", example = "DMP-数据平台")
    private String type;

    @JSONField(ordinal = 5)
    @ApiModelProperty(value = "子类型", example = "SERVICE")
    private String subType;

    @JSONField(ordinal = 6)
    @ApiModelProperty(value = "权限分组，eg:P,S", example = "P")
    private String group;

    @JSONField(ordinal = 6)
    @ApiModelProperty(value = "权限code,前端使用", example = "list_sys_role")
    private String code;

    @JSONField(ordinal = 7)
    @ApiModelProperty(value = "是否有权限", example = "true")
    private Boolean checked;

    public SysResourceVo(Long id, Long parentId, String name, String remark, String url,String group,String code) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.remark = remark;
        this.parentId = parentId;
        this.group = group;
        this.code = code;
    }

    public SysResourceVo(SysResourcePo po){
        this.id = po.getId();
        this.url = po.getUrl();
        this.name = po.getName();
        this.remark = po.getRemark();
        this.parentId = po.getParentId();
        this.type = po.getType();
        this.subType = po.getSubType();
        this.group = po.getGroup();
        this.code = po.getCode();
    }

}
