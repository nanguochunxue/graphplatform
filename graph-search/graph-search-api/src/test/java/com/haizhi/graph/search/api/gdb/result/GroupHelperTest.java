package com.haizhi.graph.search.api.gdb.result;

import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.search.api.model.qo.GdbAtlasQo;
import com.haizhi.graph.server.api.gdb.search.result.tree.GTreeBuilder;
import com.haizhi.graph.server.api.gdb.search.result.tree.GTreeNode;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/6/13.
 */
public class GroupHelperTest {

    @Test
    public void groupByDirection(){
        String rootVertexId = "Company/7908891a00d02b29354c4dd5147de439";
        List<Map<String, Object>> dataList = FileUtils.readListMap("4_arango_result.json");
        GTreeNode root = GTreeBuilder.getBySingleVertex(rootVertexId, dataList);
        JSONUtils.println(root);

        GdbAtlasQo searchQo = FileUtils.readJSONObject("4_api.json", GdbAtlasQo.class);
        GTreeNode parent = root.clone();
        for (GTreeNode node : root.getChildren()) {
            List<GTreeNode> newNodes = GroupHelper.groupByDirection(node.getEdges(), parent, node, searchQo);
            JSONUtils.println(newNodes);
        }
    }
}
