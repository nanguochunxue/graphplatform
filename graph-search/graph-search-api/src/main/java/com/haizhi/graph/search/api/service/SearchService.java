package com.haizhi.graph.search.api.service;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.search.api.model.qo.*;
import com.haizhi.graph.search.api.model.vo.*;

/**
 * Created by chengmo on 2019/4/23.
 */
public interface SearchService {

    Response<SearchVo> search(SearchQo searchQo);

    Response<GdbSearchVo> searchGdb(GdbSearchQo searchQo);

    Response<GdbAtlasVo> searchAtlas(GdbAtlasQo searchQo);

    Response<KeySearchVo> searchByKeys(KeySearchQo searchQo);

    Response<NativeSearchVo> searchNative(NativeSearchQo searchQo);

    Response<Object> executeProxy(String request);
}
