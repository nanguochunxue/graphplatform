package com.haizhi.graph.dc.core.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.haizhi.graph.dc.core.model.po.DcTaskInstancePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by chengangxiong on 2019/02/15
 */
@Data
@NoArgsConstructor
@ApiModel(value = "总体进度显示TaskProcessVo", description = "用于展示任务进度")
public class TaskProcessVo {

    @JsonIgnore
    private static final int _scale = 3;
    @JsonIgnore
    private static final String COLOR_RED = "red";
    @JsonIgnore
    private static final String COLOR_GREEN = "green";

    @ApiModelProperty(value = "taskId")
    private Long taskId;

    @ApiModelProperty(value = "taskInstanceId")
    private Long taskInstanceId;

    @ApiModelProperty(value = "总条数")
    private Integer totalRows;

    @ApiModelProperty(value = "gdb处理条数")
    private Integer gdbAffectedRows;

    @ApiModelProperty(value = "gdb处理进度")
    private Double gdbAffectedRate;

    @ApiModelProperty(value = "hbase处理条数")
    private Integer hbaseAffectedRows;

    @ApiModelProperty(value = "hbase处理进度")
    private Double hbaseAffectedRate;

    @ApiModelProperty(value = "es处理条数")
    private Integer esAffectedRows;

    @ApiModelProperty(value = "es处理进度")
    private Double esAffectedRate;

    @ApiModelProperty(value = "总体处理条数")
    private Integer totalAffectedRows;

    @ApiModelProperty(value = "总体处理进度")
    private Double totalRate;

    @ApiModelProperty(value = "任务状态", example = "RUNNING")
    private String taskDisplayState;

    public TaskProcessVo(DcTaskInstancePo instancePo) {
        this.taskId = instancePo.getTaskId();
        this.taskInstanceId = instancePo.getId();
        this.totalRows = instancePo.getTotalRows();
        this.gdbAffectedRows = instancePo.getGdbAffectedRows();
        this.hbaseAffectedRows = instancePo.getHbaseAffectedRows();
        this.esAffectedRows = instancePo.getEsAffectedRows();
        this.totalAffectedRows = gdbAffectedRows + hbaseAffectedRows + esAffectedRows;
        this.gdbAffectedRate = doRate(gdbAffectedRows, totalRows);
        this.hbaseAffectedRate = doRate(hbaseAffectedRows, totalRows);
        this.esAffectedRate = doRate(esAffectedRows, totalRows);
        this.totalRate = doRate(totalAffectedRows, totalRows);
    }

    public TaskProcessVo(DcTaskInstancePo instancePo, String taskDisplayState, boolean useGdb, boolean useSearch, boolean useHBase) {
        this.taskDisplayState = taskDisplayState;
        this.taskId = instancePo.getTaskId();
        this.taskInstanceId = instancePo.getId();
        this.totalRows = instancePo.getTotalRows();
        int totalStore = 0;
        this.totalAffectedRows = 0;
        if (!useGdb) {
            this.gdbAffectedRate = -1d;
            this.gdbAffectedRows = 0;
        } else {
            totalStore++;
            this.gdbAffectedRows = instancePo.getGdbAffectedRows();
            totalAffectedRows += gdbAffectedRows + instancePo.getGdbErrorRows();
            this.gdbAffectedRate = doRate(gdbAffectedRows, totalRows);
        }
        if (!useSearch) {
            this.esAffectedRate = -1d;
            this.esAffectedRows = 0;
        } else {
            totalStore++;
            this.esAffectedRows = instancePo.getEsAffectedRows();
            totalAffectedRows += esAffectedRows + instancePo.getEsErrorRows();
            this.esAffectedRate = doRate(esAffectedRows, totalRows);
        }
        if (!useHBase) {
            this.hbaseAffectedRate = -1d;
            this.hbaseAffectedRows = 0;
        } else {
            totalStore++;
            this.hbaseAffectedRows = instancePo.getHbaseAffectedRows();
            totalAffectedRows += hbaseAffectedRows + instancePo.getHbaseErrorRows();
            this.hbaseAffectedRate = doRate(hbaseAffectedRows, totalRows);
        }
        this.totalRate = doRate(esAffectedRows + gdbAffectedRows + hbaseAffectedRows, totalRows * totalStore);
    }

    private Double doRate(Integer affectedRows, Integer totalRows) {
        if (affectedRows == 0 || totalRows == 0) {
            return 0d;
        }
        if (affectedRows == -1 || totalRows == -1) {
            return -1d;
        }
        BigDecimal affectedRowsBigDecimal = new BigDecimal(affectedRows);
        BigDecimal totalRowsBigDecimal = new BigDecimal(totalRows);
        BigDecimal result = affectedRowsBigDecimal.divide(totalRowsBigDecimal, _scale, BigDecimal.ROUND_HALF_UP);
        return result.doubleValue();
    }
}
