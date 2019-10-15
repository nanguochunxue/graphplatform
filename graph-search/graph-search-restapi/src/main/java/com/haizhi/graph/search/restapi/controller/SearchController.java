package com.haizhi.graph.search.restapi.controller;


import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.web.constant.MediaType;
import com.haizhi.graph.search.api.model.qo.*;
import com.haizhi.graph.search.api.model.vo.*;
import com.haizhi.graph.search.api.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created by tanghaiyang on 2019/4/2.
 */
@Api(description = "[统一查询")
@RestController
@RequestMapping("/")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @ApiOperation(value = "通用搜索")
    @PostMapping(path = "/search", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response<SearchVo> search(@RequestBody @Valid SearchQo searchQo){
        return searchService.search(searchQo);
    }

    /*@ApiOperation(value = "图谱查询")
    @PostMapping(path = "/searchAtlas", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response<GdbAtlasVo> searchAtlas(@RequestBody @Valid GdbAtlasQo searchQo){
        return searchService.searchAtlas(searchQo);
    }*/

    @ApiOperation(value = "图查询")
    @PostMapping(path = "/searchGdb", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response<GdbSearchVo> searchGdb(@RequestBody @Valid GdbSearchQo searchQo){
        return searchService.searchGdb(searchQo);
    }

    @ApiOperation(value = "主键搜索")
    @PostMapping(path = "/searchByKeys", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response<KeySearchVo> searchByKeys(@RequestBody @Valid KeySearchQo searchQo){
        return searchService.searchByKeys(searchQo);
    }

    @ApiOperation(value = "原生搜索")
    @PostMapping(path = "/searchNative", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response<NativeSearchVo> searchNative(@RequestBody @Valid NativeSearchQo searchQo){
        return searchService.searchNative(searchQo);
    }

    @ApiOperation(value = "代理搜索")
    @PostMapping(path = "/executeProxy", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response<Object> executeProxy(@RequestBody @Valid String request){
        return searchService.executeProxy(request);
    }
}