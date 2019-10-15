package com.haizhi.graph.search.restapi.manager;

import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.search.api.constant.GdbVersion;
import com.haizhi.graph.search.api.model.qo.GdbAtlasQo;
import com.haizhi.graph.search.api.model.qo.GdbSearchQo;
import com.haizhi.graph.search.api.model.qo.KeySearchQo;
import com.haizhi.graph.search.api.model.qo.NativeSearchQo;
import com.haizhi.graph.search.api.model.vo.GdbAtlasVo;
import com.haizhi.graph.search.api.model.vo.GdbSearchVo;
import com.haizhi.graph.search.api.model.vo.KeySearchVo;
import com.haizhi.graph.search.api.model.vo.NativeSearchVo;
import com.haizhi.graph.search.restapi.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by tanghaiyang on 2019/4/25.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles(value = "")
public class GdbManagerTest {

    @Autowired
    private GdbManager gdbManager;

    @Test
    public void searchAtlas(){
        String api = "conditions/standardAtlasExpand.json";
        GdbAtlasQo gdbAtlasQo = FileUtils.readJSONObject(api, GdbAtlasQo.class);
        JSONUtils.println(gdbAtlasQo);
        GdbVersion version = GdbVersion.ATLAS;
        Response<GdbAtlasVo> response = gdbManager.searchAtlas(gdbAtlasQo, version);
        JSONUtils.println(response);
    }

    @Test
    public void searchGdb_kExpand(){
        String api = "conditions/kExpand.json";
        GdbSearchQo gdbSearchQo = FileUtils.readJSONObject(api, GdbSearchQo.class);
        GdbVersion version = GdbVersion.ATLAS;
        Response<GdbSearchVo> response = gdbManager.searchGdb(gdbSearchQo, version);
        JSONUtils.println(response);
    }

    @Test
    public void searchGdb_shortestPath(){
        String api = "conditions/shortestPath.json";
        GdbSearchQo gdbSearchQo = FileUtils.readJSONObject(api, GdbSearchQo.class);
        GdbVersion version = GdbVersion.ATLAS;
        Response<GdbSearchVo> response = gdbManager.searchGdb(gdbSearchQo, version);
        JSONUtils.println(response);
    }

    @Test
    public void searchGdb_fullPath(){
        String api = "conditions/fullPath.json";
        GdbSearchQo gdbSearchQo = FileUtils.readJSONObject(api, GdbSearchQo.class);
        GdbVersion version = GdbVersion.ATLAS;
        Response<GdbSearchVo> response = gdbManager.searchGdb(gdbSearchQo, version);
        JSONUtils.println(response);
    }
    @Test
    public void searchByKeys(){
        String condition = FileUtils.readTxtFile("conditions/searchByKeys.json");
        KeySearchQo keySearchQo = JSONObject.parseObject(condition, KeySearchQo.class);
        JSONUtils.println(keySearchQo);
        GdbVersion version = GdbVersion.ATLAS;
        Response<KeySearchVo> response = gdbManager.searchByKeys(keySearchQo, version);
        JSONUtils.println(response);
    }

    @Test
    public void searchNative(){
        NativeSearchQo nativeSearchQo = new NativeSearchQo();
        nativeSearchQo.setGraph("test");
        nativeSearchQo.setQuery(FileUtils.readTxtFile("conditions/kExpand.aql"));
        JSONUtils.println(nativeSearchQo);
        GdbVersion version = GdbVersion.ATLAS;
        Response<NativeSearchVo> response = gdbManager.searchNative(nativeSearchQo, version);
        JSONUtils.println(response);
    }
}
