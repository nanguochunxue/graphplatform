package com.haizhi.graph.search.arango.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.dc.core.model.qo.DcVertexEdgeQo;
import com.haizhi.graph.dc.core.service.DcVertexEdgeService;
import com.haizhi.graph.search.api.gdb.service.GdbService;
import com.haizhi.graph.search.api.model.qo.GdbAtlasQo;
import com.haizhi.graph.search.api.model.qo.GdbSearchQo;
import com.haizhi.graph.search.api.model.qo.KeySearchQo;
import com.haizhi.graph.search.api.model.qo.NativeSearchQo;
import com.haizhi.graph.search.api.model.vo.GdbAtlasVo;
import com.haizhi.graph.search.api.model.vo.GdbSearchVo;
import com.haizhi.graph.search.api.model.vo.KeySearchVo;
import com.haizhi.graph.search.api.model.vo.NativeSearchVo;
import com.haizhi.graph.search.arango.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by tanghaiyang on 2019/4/2.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles(value = "")
public class GdbServiceTest {

    private static final GLog LOG = LogFactory.getLogger(GLog.class);

    @Autowired
    GdbService gdbService;

    @Autowired
    private DcVertexEdgeService dcVertexEdgeService;

    @Test
    public void searchAtlas(){
        String api = "api/searchAtlas.json";
        GdbAtlasQo gdbAtlasQo = FileUtils.readJSONObject(api, GdbAtlasQo.class);
        Response<GdbAtlasVo> response = gdbService.searchAtlas(gdbAtlasQo);
        LOG.info(JSON.toJSONString(response, true));
    }

    @Test
    public void searchNative() {
        NativeSearchQo qo = new NativeSearchQo();
        qo.setGraph("test");
        qo.setQuery(FileUtils.readTxtFile("api/searchNative_kExpand.aql"));
        Response<NativeSearchVo> response = gdbService.searchNative(qo);
        LOG.info(JSON.toJSONString(response, true));
    }

    @Test
    public void searchByKeys() {
        String condition = FileUtils.readTxtFile("api/searchByKeys.json");
        KeySearchQo keySearchQo = JSONObject.parseObject(condition, KeySearchQo.class);
        Response<KeySearchVo> response = gdbService.searchByKeys(keySearchQo);
        LOG.info(JSON.toJSONString(response, true));
    }

    @Test
    public void searchGdb_kExpand() {
        String api = "api/searchGdb_kExpand.json";
        GdbSearchQo gdbSearchQo = FileUtils.readJSONObject(api, GdbSearchQo.class);
        Response<GdbSearchVo> response = gdbService.searchGdb(gdbSearchQo);
        LOG.info(JSON.toJSONString(response, true));
    }

    @Test
    public void searchGdb_shortestPath() {
        String api = "api/searchGdb_shortestPath.json";
        GdbSearchQo gdbSearchQo = FileUtils.readJSONObject(api, GdbSearchQo.class);
        Response<GdbSearchVo> response = gdbService.searchGdb(gdbSearchQo);
        LOG.info(JSON.toJSONString(response, true));
    }

    @Test
    public void searchGdb_fullPath() {
        String api = "api/searchGdb_fullPath.json";
        GdbSearchQo gdbSearchQo = FileUtils.readJSONObject(api, GdbSearchQo.class);
        Response<GdbSearchVo> response = gdbService.searchGdb(gdbSearchQo);
        LOG.info(JSON.toJSONString(response, true));
    }

    @Test
    public void wrapVertices(){
        GdbSearchQo qo = new GdbSearchQo();
        qo.setGraph("test");
        qo.setEdgeTables(Sets.newHashSet("te_officer","te_guarantee"));
        wrapVertices(qo);
        LOG.info("qo:\n{0}", JSON.toJSONString(qo, true));
    }

    private void wrapVertices(GdbSearchQo searchQo){
        DcVertexEdgeQo dcVertexEdgeQo = new DcVertexEdgeQo();
        dcVertexEdgeQo.setGraph(searchQo.getGraph());
        dcVertexEdgeQo.setEdges(searchQo.getEdgeTables());
        Response<Set<String>> response = dcVertexEdgeService.findCollections(dcVertexEdgeQo);
        Set<String> vertexTables = searchQo.getVertexTables();
        if(Objects.isNull(vertexTables)) vertexTables = new HashSet<>();
        Set<String> data = response.getPayload().getData();
        if(Objects.isNull(data) || data.isEmpty()) return;
        vertexTables.addAll(data);
        LOG.info("add extra vertex collections from dc_vertex_edge to resolve bug about AQL:with error, collections:\n{0}",data.toString());
        searchQo.setVertexTables(vertexTables);
    }


}
