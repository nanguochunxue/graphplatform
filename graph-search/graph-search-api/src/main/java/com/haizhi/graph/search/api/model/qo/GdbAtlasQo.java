package com.haizhi.graph.search.api.model.qo;

import com.haizhi.graph.common.model.BaseQo;
import com.haizhi.graph.search.api.gdb.model.GdbRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;
import java.util.Set;

/**
 * Created by chengmo on 2018/6/5.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "图谱查询对象GdbAtlasQo", description = "图谱查询")
public class GdbAtlasQo extends BaseQo {

    @ApiModelProperty(value = "图数据库", required = true)
    private String graph;

    @ApiModelProperty(value = "起始顶点集")
    private Set<String> startVertices;

    @ApiModelProperty(value = "终止顶点集")
    private Set<String> endVertices;

    @ApiModelProperty(value = "顶点表集")
    private Set<String> vertexTables;

    @ApiModelProperty(value = "边表集")
    private Set<String> edgeTables;

    @ApiModelProperty(value = "查询方向", required = true, example = "ANY")
    private String direction = "ANY";

    @ApiModelProperty(value = "查询深度", required = true, example = "ANY")
    private int maxDepth = 2;

    @ApiModelProperty(value = "limit约束起始", example = "0")
    private int offset = -1;

    @ApiModelProperty(value = "limit约束终止", example = "10")
    private int size = -1;

    @ApiModelProperty(value = "查询结果最大数量，size和maxSize较大者有效", example = "10")
    private int maxSize = -1;

    @ApiModelProperty(value = "返回结果类型", example = "10")
    private String resultType; //default|tree|path

    @ApiModelProperty(value = "查询规则", example = "10")
    private Map<String, GdbRule> rule;

    @ApiModelProperty(value = "过滤条件", example = "10")
    private Map<String, Object> filter;

    public boolean needAgg(){
        if (rule == null){
            return false;
        }
        for (GdbRule gdbRule : rule.values()) {
            if (gdbRule.needAgg()){
                return true;
            }
        }
        return false;
    }

    public boolean needAggNewVertices(){
        if (rule == null){
            return false;
        }
        for (GdbRule gdbRule : rule.values()) {
            if (gdbRule.needAggNewVertices()){
                return true;
            }
        }
        return false;
    }

    public boolean needAgg(String edgeSchema){
        GdbRule gdbRule = rule.get(edgeSchema);
        if (gdbRule == null){
            throw new IllegalArgumentException("rule missing with " + edgeSchema);
        }
        return gdbRule.needAgg();
    }

    public boolean needAggNewVertices(String edgeSchema){
        GdbRule gdbRule = rule.get(edgeSchema);
        if (gdbRule == null){
            throw new IllegalArgumentException("rule missing with " + edgeSchema);
        }
        return gdbRule.needAggNewVertices();
    }
}
