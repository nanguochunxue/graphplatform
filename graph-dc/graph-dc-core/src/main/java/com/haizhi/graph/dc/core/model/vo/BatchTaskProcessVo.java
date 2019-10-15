package com.haizhi.graph.dc.core.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by chengangxiong on 2019/02/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "批量任务进度显示BatchTaskProcessVo", description = "用于展示批量任务进度")
public class BatchTaskProcessVo {

    private List<TaskProcessVo> processVoList;
}
