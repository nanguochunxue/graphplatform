package com.haizhi.graph.dc.core.model.vo;

import com.haizhi.graph.dc.core.model.po.DcTaskInstancePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by chengangxiong on 2019/02/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "api接入查询显示ApiInboundVo", description = "")
public class ApiInboundVo {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "统计时间", example = "2019-01-05")
    private String operateDt;

    @ApiModelProperty(value = "总数据大小", example = "1024")
    private Integer totalSize;

    @ApiModelProperty(value = "总行数", example = "33")
    private Integer totalRows;

    @ApiModelProperty(value = "错误数", example = "9")
    private Integer errorRows;

    @ApiModelProperty(value = "错误记录", example = "")
    private String errorRecord;

    public ApiInboundVo(DcTaskInstancePo instancePo) {
        this.id = instancePo.getId();
        this.operateDt = instancePo.getOperateDt() == null? "" : instancePo.getOperateDt().toString();
        this.totalRows = instancePo.getTotalRows();
        this.totalSize = instancePo.getTotalSize();
    }
}
