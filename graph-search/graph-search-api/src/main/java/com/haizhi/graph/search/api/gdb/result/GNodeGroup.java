package com.haizhi.graph.search.api.gdb.result;

import com.haizhi.graph.server.api.gdb.search.result.tree.GTreeNode;
import lombok.Data;

/**
 * Created by chengmo on 2018/6/13.
 */
@Data
public class GNodeGroup {
    private GTreeNode fromNode;
    private GTreeNode toNode;
}
