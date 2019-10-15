package com.haizhi.graph.dc.core.model.vo;

import com.haizhi.graph.common.model.BaseVo;
import com.haizhi.graph.dc.core.constant.TaskMetaType;
import com.haizhi.graph.dc.core.model.po.DcTaskMetaPo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by chengangxiong on 2019/04/23
 */
@Data
@NoArgsConstructor
@ApiModel(value = "任务字段映射关系显示对象DcTaskMetaVo", description = "用于展示任务字段映射关系")
public class DcTaskMetaVo extends BaseVo {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "任务ID", example = "100")
    private Long taskId;

    @ApiModelProperty(value = "映射类型", example = "FIELD")
    private TaskMetaType type;

    @ApiModelProperty(value = "源字段", example = "name")
    private String srcField;

    @ApiModelProperty(value = "目标字段", example = "name_test")
    private String dstField;

    @ApiModelProperty(value = "序号", example = "1")
    private int sequence = 1;

    public DcTaskMetaVo(DcTaskMetaPo dcTaskMetaPo) {
        this.id = dcTaskMetaPo.getId();
        this.taskId = dcTaskMetaPo.getTaskId();
        this.type = dcTaskMetaPo.getType();
        this.srcField = dcTaskMetaPo.getSrcField();
        this.dstField = dcTaskMetaPo.getDstField();
        this.sequence = dcTaskMetaPo.getSequence();
    }
}
