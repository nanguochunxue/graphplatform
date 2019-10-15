package com.haizhi.graph.search.es.controller;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.web.constant.MediaType;
import com.haizhi.graph.search.api.es.service.EsService;
import com.haizhi.graph.search.api.model.qo.KeySearchQo;
import com.haizhi.graph.search.api.model.qo.NativeSearchQo;
import com.haizhi.graph.search.api.model.qo.SearchQo;
import com.haizhi.graph.search.api.model.vo.KeySearchVo;
import com.haizhi.graph.search.api.model.vo.NativeSearchVo;
import com.haizhi.graph.search.api.model.vo.SearchVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created by chengmo on 2018/1/4.
 */
@Api(description = "[搜索]API")
@RestController
@RequestMapping(value = "/api")
public class SearchController {

    @Autowired
    private EsService esService;

    @ApiOperation(value = "通用搜索", notes = "输入 {\"graph\": \"crm_dev2\",\"keyword\": \"海致星图\"}")
    @PostMapping(path = "/search", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response<SearchVo> search(@RequestBody SearchQo searchQo){
        return esService.search(searchQo);
    }

    @ApiOperation(value = "主键搜索", notes = "{ \"graph\":\"graph_ccb_dev\", \"schemaKeys\": { \"te_repayment\": [ \"006F52E\" ], \"te_address\": [ \"006FA8D3\" ] } }")
    @PostMapping(path = "/searchByKeys", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response<KeySearchVo> searchByKeys(@RequestBody KeySearchQo searchQo){
        return esService.searchByKeys(searchQo);
    }

    @PostMapping(path = "/searchNative", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response<NativeSearchVo> searchNative(@RequestBody NativeSearchQo searchQo){
        return esService.searchNative(searchQo);
    }

    @ApiOperation(value = "代理搜索")
    @PostMapping(path = "/executeProxy", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response<Object> executeProxy(@RequestBody @Valid String request){
        return esService.executeProxy(request);
    }

}
