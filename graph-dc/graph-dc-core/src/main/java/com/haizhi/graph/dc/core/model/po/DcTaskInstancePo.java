package com.haizhi.graph.dc.core.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.haizhi.graph.common.model.BasePo;
import com.haizhi.graph.dc.core.constant.OperateType;
import com.haizhi.graph.dc.core.constant.TaskInstanceState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by chengangxiong on 2019/01/29
 */
@Data
@Entity
@Table(name = "dc_task_instance")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class DcTaskInstancePo extends BasePo {

    @NotNull
    @Column(name = "task_id")
    private Long taskId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "operate_type", length = 30)
    private OperateType operateType;

    @Column(name = "operate_dt", length = 30, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date operateDt;

    @NotNull
    @Convert(converter = TaskInstanceState.Converter.class)
    @Column(name = "state")
    private TaskInstanceState state;

    @NotNull
    @Column(name = "total_size")
    private Integer totalSize = 0;

    @NotNull
    @Column(name = "total_rows")
    private Integer totalRows = 0;

    @NotNull
    @Column(name = "error_rows")
    private Integer errorRows = 0;

    @NotNull
    @Column(name = "hbase_affected_rows")
    private Integer hbaseAffectedRows = 0;

    @NotNull
    @Column(name = "hbase_error_rows")
    private Integer hbaseErrorRows = 0;

    @NotNull
    @Column(name = "es_affected_rows")
    private Integer esAffectedRows = 0;

    @NotNull
    @Column(name = "es_error_rows")
    private Integer esErrorRows = 0;

    @NotNull
    @Column(name = "gdb_affected_rows")
    private Integer gdbAffectedRows = 0;

    @NotNull
    @Column(name = "gdb_error_rows")
    private Integer gdbErrorRows = 0;

    @Column(name = "yarn_app_info")
    private String yarnAppInfo;

    @Column(name = "error_mode", length = 11)
    private Integer errorMode = -1;

    public boolean success(boolean useHBase, boolean useSearch, boolean useGdb) {
        boolean success = true;
        if (useHBase) {
            success &= (totalRows.compareTo(hbaseAffectedRows) == 0);
        }
        if (useSearch) {
            success &= (totalRows.compareTo(esAffectedRows) == 0);
        }
        if (useGdb) {
            success &= (totalRows.compareTo(gdbAffectedRows) == 0);
        }
        return success;
    }

    public boolean failed(boolean useHBase, boolean useSearch, boolean useGdb) {
        boolean failed = (errorRows + hbaseErrorRows + esErrorRows + gdbErrorRows) > 0;
        if (useHBase) {
            failed &= ((totalRows <= hbaseAffectedRows + hbaseErrorRows + errorRows));
        }
        if (useSearch) {
            failed &= ((totalRows <= esAffectedRows + esErrorRows + errorRows));
        }
        if (useGdb) {
            failed &= ((totalRows <= gdbAffectedRows + gdbErrorRows + errorRows));
        }
        return failed;
    }
}
