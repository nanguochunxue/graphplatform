package com.haizhi.graph.server.api.gdb.search.result;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.server.api.gdb.search.result.tree.GTreeBuilder;
import com.haizhi.graph.server.api.gdb.search.result.tree.GTreeNode;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/6/7.
 */
public class GResultBuilderTest {

    @Test
    public void get(){
        List<Map<String, Object>> dataList = FileUtils.readListMap("gdb_result.json");
        GResult gResult = GResultBuilder.get(dataList);
        System.out.println(JSON.toJSONString(gResult, true));
    }

    @Test
    public void getByTree(){
        String rootVertexId = "Company/v1";
        List<Map<String, Object>> dataList = FileUtils.readListMap("gdb_result.json");
        GTreeNode root = GTreeBuilder.getBySingleVertex(rootVertexId, dataList);
        System.out.println(JSON.toJSONString(root, true));

        GResult gResult = GResultBuilder.getByTree(root);
        System.out.println(JSON.toJSONString(gResult, true));
    }
}
