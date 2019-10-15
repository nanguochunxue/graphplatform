package com.haizhi.graph.dc.core.model.suo;

import com.haizhi.graph.dc.core.constant.TaskMetaType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chengangxiong on 2019/04/23
 */
@Data
@ApiModel(value = "任务的映射字段DcTaskMetaSuo", description = "任务的映射字段")
public class DcTaskMetaSuo {

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
}
