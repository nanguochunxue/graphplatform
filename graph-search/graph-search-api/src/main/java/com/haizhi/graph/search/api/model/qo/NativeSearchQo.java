package com.haizhi.graph.search.api.model.qo;

import com.haizhi.graph.common.model.BaseQo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

/**
 * Created by chengmo on 2019/4/23.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "原生搜索NativeSearchQo", description = "原生搜索条件")
public class NativeSearchQo extends BaseQo {

    @ApiModelProperty(value = "图域名", example = "demo_graph")
    private String graph;

    @ApiModelProperty(value = "原生查询条件", required = true, example = "嵌套json对象{\"from\": 0,\"size\": 10,\"query\": {\"match_all\": {}}}")
    private Object query;

    @ApiModelProperty(value = "图查询参数")
    private Map<String, Object> parameters;

    @ApiModelProperty(value = "图查询扩展参数,比如profile用于类似sql的explain关键字效果")
    private Map<String, Object> options;

}
