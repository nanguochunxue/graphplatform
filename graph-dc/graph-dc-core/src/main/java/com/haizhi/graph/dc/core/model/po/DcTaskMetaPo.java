package com.haizhi.graph.dc.core.model.po;

import com.haizhi.graph.common.model.BasePo;
import com.haizhi.graph.dc.core.constant.TaskMetaType;
import com.haizhi.graph.dc.core.model.suo.DcTaskMetaSuo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by chengangxiong on 2019/04/23
 */
@Data
@Entity
@Table(name = "dc_task_meta")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class DcTaskMetaPo extends BasePo {

    @NotNull
    @Column(name = "taskId")
    private Long taskId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30)
    private TaskMetaType type;

    @NotNull
    @Column(name = "src_field", length = 128)
    private String srcField;

    @Column(name = "dst_field", length = 128)
    private String dstField;

    @NotNull
    @Column(name = "sequence", length = 6)
    private int sequence = 1;

    public DcTaskMetaPo(DcTaskMetaSuo dcTaskMetaSuo) {
        this.id = dcTaskMetaSuo.getId();
        this.taskId = dcTaskMetaSuo.getTaskId();
        this.type = dcTaskMetaSuo.getType();
        this.srcField = dcTaskMetaSuo.getSrcField();
        this.dstField = dcTaskMetaSuo.getDstField();
        this.sequence = dcTaskMetaSuo.getSequence();
    }
}
