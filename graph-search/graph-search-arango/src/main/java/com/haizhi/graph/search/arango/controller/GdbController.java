package com.haizhi.graph.search.arango.controller;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.web.constant.MediaType;
import com.haizhi.graph.search.api.gdb.service.GdbService;
import com.haizhi.graph.search.api.model.qo.GdbAtlasQo;
import com.haizhi.graph.search.api.model.qo.GdbSearchQo;
import com.haizhi.graph.search.api.model.qo.KeySearchQo;
import com.haizhi.graph.search.api.model.qo.NativeSearchQo;
import com.haizhi.graph.search.api.model.vo.GdbAtlasVo;
import com.haizhi.graph.search.api.model.vo.GdbSearchVo;
import com.haizhi.graph.search.api.model.vo.KeySearchVo;
import com.haizhi.graph.search.api.model.vo.NativeSearchVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by tanghaiyang on 2019/4/2.
 */
@RestController
@RequestMapping("/api")
public class GdbController {

    @Autowired
    private GdbService gdbService;

    @PostMapping(path = "/searchGdb", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response<GdbSearchVo> searchGdb(@RequestBody GdbSearchQo searchQo){
        return gdbService.searchGdb(searchQo);
    }

    @PostMapping(path = "/searchAtlas", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response<GdbAtlasVo> searchAtlas(@RequestBody GdbAtlasQo searchQo){
        return gdbService.searchAtlas(searchQo);
    }

    @PostMapping(path = "/searchByKeys", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response<KeySearchVo> searchByKeys(@RequestBody KeySearchQo searchQo){
        return gdbService.searchByKeys(searchQo);
    }

    @PostMapping(path = "/searchNative", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response<NativeSearchVo> searchNative(@RequestBody NativeSearchQo searchQo){
        return gdbService.searchNative(searchQo);
    }
}