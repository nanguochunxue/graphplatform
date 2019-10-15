package com.haizhi.graph.dc.core.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Create by zhoumingbing on 2019-05-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "任务执行")
public class TaskRunOnceQo {

    @ApiModelProperty(value = "任务ID", example = "111", required = true)
    private Long id;

    @ApiModelProperty(value = "错误处理方式，-1表示出错继续，大于或等于0大于该值终止", example = "-1")
    private Integer errorMode;

    @ApiModelProperty(value = "任务自定义参数，如：GreenPlum导入时的SQL中的参数")
    private Map<String, Object> params;

    public TaskRunOnceQo(Long id, Integer errorMode) {
        this.id = id;
        this.errorMode = errorMode;
    }
}
