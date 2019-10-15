package com.haizhi.graph.server.api.gdb.search.result.tree;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;
import com.haizhi.graph.common.util.FileUtils;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/6/7.
 */
public class GTreeBuilderTest {

    @Test
    public void getBySingleVertex(){
        String rootVertexId = "Company/v1";
        List<Map<String, Object>> dataList = FileUtils.readListMap("gdb_result2.json");
        GTreeNode root = GTreeBuilder.getBySingleVertex(rootVertexId, dataList);
        System.out.println(JSON.toJSONString(root, true));
    }

    @Test
    public void graphTest(){
        MutableGraph<String> graph = GraphBuilder.directed().build();
        graph.putEdge("Company/7908891a00d02b29354c4dd5147de439",
                "Company/5870542fa61a997834c7d61b829fd1ba");
        graph.putEdge("Company/5870542fa61a997834c7d61b829fd1ba",
                "Company/36d37c063ee31a5aebcc3667af028715");
        Iterable<String> topology = Traverser.forGraph(graph)
                .depthFirstPostOrder("Company/7908891a00d02b29354c4dd5147de439");
        List<String> list = Lists.newArrayList(topology.iterator());
        Collections.reverse(list);
        System.out.println(list);
    }
}
