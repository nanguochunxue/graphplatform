package com.haizhi.graph.dc.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Created by chengangxiong on 2019/04/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Crontab显示详细CrontabDescVo", description = "")
public class CrontabDescVo {

    @ApiModelProperty(value = "cron表达式", example = "0 23 * ? * 1-5 *")
    private String cron;

    @ApiModelProperty(value = "cron中文描述")
    private String description;

    @ApiModelProperty(value = "下次执行时间")
    private List<Date> nextExecuteTime;
}
