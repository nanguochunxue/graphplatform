package com.haizhi.graph.search.restapi.service;

import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.search.api.model.qo.*;
import com.haizhi.graph.search.api.model.vo.*;
import com.haizhi.graph.search.api.service.SearchService;
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
public class SearchServiceTest {

    @Autowired
    private SearchService searchService;

    @Test
    public void search(){
        String api = "api/search.json";
        SearchQo searchQo = FileUtils.readJSONObject(api, SearchQo.class);
        JSONUtils.println(searchQo);
        Response<SearchVo> response = searchService.search(searchQo);
        JSONUtils.println(response);
    }

    @Test
    public void searchAtlas(){
        String api = "api/standardAtlasExpand.json";
        GdbAtlasQo gdbAtlasQo = FileUtils.readJSONObject(api, GdbAtlasQo.class);
        JSONUtils.println(gdbAtlasQo);
        Response<GdbAtlasVo> response = searchService.searchAtlas(gdbAtlasQo);
        JSONUtils.println(response);
    }

    @Test
    public void searchGdb_kExpand() {
        String api = "api/kExpand.json";
        GdbSearchQo gdbSearchQo = FileUtils.readJSONObject(api, GdbSearchQo.class);
        Response<GdbSearchVo> response = searchService.searchGdb(gdbSearchQo);
        JSONUtils.println(response);
    }

    @Test
    public void searchGdb_shortestPath(){
        String api = "api/shortestPath.json";
        GdbSearchQo gdbSearchQo = FileUtils.readJSONObject(api, GdbSearchQo.class);
        JSONUtils.println(gdbSearchQo);
        Response<GdbSearchVo> response = searchService.searchGdb(gdbSearchQo);
        JSONUtils.println(response);
    }

    @Test
    public void searchGdb_fullPath() {
        String api = "api/fullPath.json";
        GdbSearchQo gdbSearchQo = FileUtils.readJSONObject(api, GdbSearchQo.class);
        Response<GdbSearchVo> response = searchService.searchGdb(gdbSearchQo);
        JSONUtils.println(response);
    }

    @Test
    public void searchByKeys(){
        String condition = FileUtils.readTxtFile("api/searchByKeys.json");
        KeySearchQo keySearchQo = JSONObject.parseObject(condition, KeySearchQo.class);
        JSONUtils.println(keySearchQo);
        Response<KeySearchVo> response = searchService.searchByKeys(keySearchQo);
        JSONUtils.println(response);
    }

    @Test
    public void searchNative(){
        NativeSearchQo nativeSearchQo = new NativeSearchQo();
        nativeSearchQo.setGraph("test");
        nativeSearchQo.setQuery(FileUtils.readTxtFile("api/kExpand.aql"));
        JSONUtils.println(nativeSearchQo);
        Response<NativeSearchVo> response = searchService.searchNative(nativeSearchQo);
        JSONUtils.println(response);
    }
}
