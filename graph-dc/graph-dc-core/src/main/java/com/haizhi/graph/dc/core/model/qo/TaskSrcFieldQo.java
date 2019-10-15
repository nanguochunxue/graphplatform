package com.haizhi.graph.dc.core.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by chengangxiong on 2019/04/23
 */
@Data
@ApiModel(value = "任务源字段查询对象TaskSrcFieldQo", description = "")
@NoArgsConstructor
public class TaskSrcFieldQo {

    @ApiModelProperty(value = "文件ID")
    private Long fileId;

    @ApiModelProperty(value = "文件的服务器地址")
    private String serverPath;
}
