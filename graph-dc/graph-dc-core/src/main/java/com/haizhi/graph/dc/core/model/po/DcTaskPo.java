package com.haizhi.graph.dc.core.model.po;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.model.BasePo;
import com.haizhi.graph.dc.core.constant.ExecutionType;
import com.haizhi.graph.dc.core.constant.OperateType;
import com.haizhi.graph.dc.core.constant.TaskState;
import com.haizhi.graph.dc.core.constant.TaskType;
import com.haizhi.graph.dc.core.model.suo.ApiTaskSuo;
import com.haizhi.graph.dc.core.model.suo.BatchTaskSuo;
import com.haizhi.graph.dc.core.model.suo.DcTaskSuo;
import com.haizhi.graph.dc.core.model.suo.FlumeTaskSuo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Created by chengangxiong on 2019/01/29
 */
@Data
@Entity
@Table(name = "dc_task")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class DcTaskPo extends BasePo {

    @NotNull
    @Column(name = "graph", length = 50)
    private String graph;

    @NotNull
    @Column(name = "`schema`", length = 128)
    private String schema;

    @NotNull
    @Column(name = "task_name", length = 50)
    private String taskName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", length = 30)
    private TaskType taskType;

    @NotNull
    @Convert(converter = TaskState.Convert.class)
    @Column(name = "task_state", length = 30)
    private TaskState taskState;

    @Column(name = "remark", length = 200)
    private String remark;

    @NotNull
    @Column(name = "execution_type", length = 30)
    @Enumerated(EnumType.STRING)
    private ExecutionType executionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "operate_type", length = 30)
    private OperateType operateType;

    @Column(name = "cron", length = 200)
    private String cron;

    @Column(name = "error_mode", length = 11)
    private Integer errorMode = -1;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "source", length = 300)
    private String source;

    @Column(name = "last_instance_id")
    private Long lastInstanceId;

    public DcTaskPo(DcTaskSuo suo) {
        this.id = suo.getId();
        this.graph = suo.getGraph();
        this.schema = suo.getSchema();
        this.storeId = suo.getStoreId();
        this.taskName = suo.getTaskName();
        this.source = suo.getSource();
        this.taskType = suo.getTaskType();
        this.executionType = suo.getExecutionType();
        this.operateType = suo.getOperateType();
        this.cron = suo.getCron();
        this.errorMode = suo.getErrorMode();
        if (taskType == TaskType.FILE){
            Map<String, String> sourceMap = new HashMap<>();
            sourceMap.put(suo.getSourceType().name(), this.source);
            this.source = JSON.toJSONString(sourceMap);
        }
    }

    public DcTaskPo(ApiTaskSuo suo){
        this.id = suo.getId();
        this.taskType = TaskType.API;
        this.storeId = suo.getStoreId();
    }

    public DcTaskPo(FlumeTaskSuo suo){
        this.id = suo.getId();
        this.taskType = TaskType.FLUME;
        this.storeId = suo.getStoreId();
    }

    public DcTaskPo(BatchTaskSuo suo){
        this.id = suo.getId();
        this.taskType = TaskType.BATCH;
        this.storeId = suo.getStoreId();
    }

    public void checkTaskType() {
        if (this.getTaskType() == TaskType.FILE){
            if (Objects.isNull(executionType)){
                this.setExecutionType(ExecutionType.ONCE);
            }
        }
    }
}
