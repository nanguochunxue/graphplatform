package com.haizhi.graph.search.api.gdb.model;

import com.haizhi.graph.common.model.BaseQo;
import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * Created by chengmo on 2018/6/5.
 */
@Data
public class GdbAtlasQo extends BaseQo {
    // graph
    private String graph;
    private Set<String> startVertices;
    private Set<String> endVertices;
    private Set<String> edgeTables;
    private Set<String> vertexTables;
    private String direction = "ANY";
    private int maxDepth = 2;
    private int offset = -1;
    private int size = -1;
    private int maxSize = -1;
    private String resultType;//default|tree|path

    // rule
    private Map<String, GdbRule> rule;

    // filter
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
