package com.haizhi.graph.dc.core.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by chengangxiong on 2019/04/10
 */
@Data
@ApiModel(value = "Crontab查询对象CrontabDescQo", description = "")
@NoArgsConstructor
public class CrontabDescQo {

    @ApiModelProperty(value = "cron表达式", required = true, example = "0 23 * ? * 1-5 *")
    private String cron;

    @ApiModelProperty(value = "获取次数", required = true, example = "3")
    private int num;
}
