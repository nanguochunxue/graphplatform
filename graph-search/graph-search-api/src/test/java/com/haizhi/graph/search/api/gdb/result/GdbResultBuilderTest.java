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
 * Created by chengmo on 2018/6/11.
 */
public class GdbResultBuilderTest {

    @Test
    public void rebuild_1(){
        // tree + agg
        rebuild("1_api.json", "1_arango_result1.json");
    }

    @Test
    public void rebuild_2(){
        // tree + agg
        rebuild("2_api.json", "2_arango_result.json");
    }

    @Test
    public void rebuild_3(){
        // tree
        rebuild("3_api.json", "3_arango_result1.json");
    }

    @Test
    public void rebuild_4(){
        // tree + agg + newVertices
        rebuild("4_api.json", "4_arango_result2.json");
    }

    @Test
    public void rebuild_5(){
        // tree + agg + newVertices
        rebuild("5_api.json", "5_arango_result.json");
    }

    @Test
    public void rebuild_6(){
        // tree
        rebuild("6_api.json", "6_arango_result.json");
    }

    @Test
    public void rebuild_7(){
        // tree
        rebuild("7_api.json", "7_arango_result.json");
    }

    @Test
    public void rebuild_8(){
        // tree + agg
        rebuild("8_api.json", "8_arango_result2.json");
    }

    @Test
    public void rebuild_8_filter(){
        // tree + agg
        rebuildFilter("8_api.json", "8_arango_result2.json",
                "L3KFL54944A834757515A1D521C6854F");
    }

    private void rebuildFilter(String apiFile, String resultFile, String... filterVertexIds){
        String rootVertexId = "Company/7908891a00d02b29354c4dd5147de439";
        List<Map<String, Object>> dataList = FileUtils.readListMap(resultFile);
        GTreeNode root = GTreeBuilder.getBySingleVertex(rootVertexId, dataList, filterVertexIds);
        JSONUtils.println(root);
        GdbAtlasQo searchQo = FileUtils.readJSONObject(apiFile, GdbAtlasQo.class);
        GdbResultBuilder.rebuild(searchQo, root);
        JSONUtils.println(root);
    }

    private void rebuild(String apiFile, String resultFile){
        String rootVertexId = "Company/7908891a00d02b29354c4dd5147de439";
        List<Map<String, Object>> dataList = FileUtils.readListMap(resultFile);
        GTreeNode root = GTreeBuilder.getBySingleVertex(rootVertexId, dataList);
        JSONUtils.println(root);
        GdbAtlasQo searchQo = FileUtils.readJSONObject(apiFile, GdbAtlasQo.class);
        GdbResultBuilder.rebuild(searchQo, root);
        JSONUtils.println(root);
    }
}
