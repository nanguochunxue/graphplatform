package com.haizhi.graph.dc.core.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.haizhi.graph.common.model.BaseVo;
import com.haizhi.graph.common.util.DateUtils;
import com.haizhi.graph.dc.core.constant.TaskInstanceState;
import com.haizhi.graph.dc.core.model.po.DcTaskInstancePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by chengangxiong on 2019/02/11
 */
@Data
@NoArgsConstructor
@ApiModel(value = "任务实例显示对象DcTaskInstanceVo", description = "用于展示任务实例信息")
public class DcTaskInstanceVo extends BaseVo {

    @JsonIgnore
    private static final int _scale = 3;

    @ApiModelProperty(value = "实例id", example = "28")
    private Long id;

    @ApiModelProperty(value = "任务总行数", example = "100")
    private Integer totalRows;

    @ApiModelProperty(value = "任务大小", example = "1024")
    private Integer totalSize;

    @ApiModelProperty(value = "最后执行时间", example = "2018-12-24 14:31:27")
    private String lastRunTime;

    @ApiModelProperty(value = "是否有错误", example = "false")
    private boolean hasError = false;

    @ApiModelProperty(value = "导入错误数量", example = "false")
    private Integer errorRows = 0;

    @ApiModelProperty(value = "HBase导入成功数据量", example = "50")
    private Integer hbaseAffectedRows = 0;

    @ApiModelProperty(value = "HBase导入错误数据量", example = "50")
    private Integer hbaseErrorRows = 0;

    @ApiModelProperty(value = "ES导入成功数据量", example = "50")
    private Integer esAffectedRows = 0;

    @ApiModelProperty(value = "ES导入错误数据量", example = "50")
    private Integer esErrorRows = 0;

    @ApiModelProperty(value = "Arango导入成功数据量", example = "50")
    private Integer gdbAffectedRows = 0;

    @ApiModelProperty(value = "Arango导入错误数据量", example = "50")
    private Integer gdbErrorRows = 0;


    public DcTaskInstanceVo(DcTaskInstancePo po) {
        super(po);
        this.id = po.getId();
        this.totalRows = po.getTotalRows();
        this.totalSize = po.getTotalSize();
        this.lastRunTime = DateUtils.formatLocal(po.getUpdatedDt() == null ? po.getCreatedDt() : po.getUpdatedDt());
        hasError = po.getErrorRows() != 0 || po.getState() == TaskInstanceState.INTERRUPTED || po.getState() == TaskInstanceState.FAILED;
    }

    public static DcTaskInstanceVo create(DcTaskInstancePo po) {
        DcTaskInstanceVo vo = new DcTaskInstanceVo(po);
        vo.hasError = po.getErrorRows() != 0;
        vo.errorRows = po.getErrorRows();
        vo.hbaseAffectedRows = po.getHbaseAffectedRows();
        vo.hbaseErrorRows = po.getHbaseErrorRows();
        vo.esAffectedRows = po.getEsAffectedRows();
        vo.esErrorRows = po.getEsErrorRows();
        vo.gdbAffectedRows = po.getGdbAffectedRows();
        vo.gdbErrorRows = po.getGdbErrorRows();

        return vo;
    }
}
