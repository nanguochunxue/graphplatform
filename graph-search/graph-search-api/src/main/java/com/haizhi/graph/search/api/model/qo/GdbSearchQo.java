package com.haizhi.graph.search.api.model.qo;

import com.haizhi.graph.common.model.BaseQo;
import com.haizhi.graph.search.api.constant.Direction;
import com.haizhi.graph.search.api.gdb.constant.GdbQueryType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;
import java.util.Set;

/**
 * Created by chengmo on 2019/1/21.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "图查询对象GdbSearchQo", description = "图查询")
public class GdbSearchQo extends BaseQo {

    @ApiModelProperty(value = "图数据库", required = true)
    private String graph;

    @ApiModelProperty(value = "查询类型", required = true, example = "K_EXPAND")
    private GdbQueryType type;

    @ApiModelProperty(value = "起始顶点集")
    private Set<String> startVertices;

    @ApiModelProperty(value = "终止顶点集")
    private Set<String> endVertices;

    @ApiModelProperty(value = "顶点表集")
    private Set<String> vertexTables;

    @ApiModelProperty(value = "边表集")
    private Set<String> edgeTables;

    @ApiModelProperty(value = "查询方向", required = true, example = "ANY")
    private Direction direction = Direction.ANY;

    @ApiModelProperty(value = "查询深度", required = true, example = "ANY")
    private int maxDepth = 2;

    @ApiModelProperty(value = "aql limit约束起始", example = "0")
    private int offset = -1;

    @ApiModelProperty(value = "aql limit约束终止", example = "10")
    private int size = -1;

    @ApiModelProperty(value = "查询结果分页返回时，每页数据量", example = "10")
    private int maxSize = -1;

    @ApiModelProperty(value = "查询超时时间，单位：秒", example = "15")
    private int timeout = 15;

    @ApiModelProperty(value = "查询规则", example = "10")
    private Map<String, Object> rule;

    @ApiModelProperty(value = "过滤条件", example = "10")
    private Map<String, Object> filter;

}
