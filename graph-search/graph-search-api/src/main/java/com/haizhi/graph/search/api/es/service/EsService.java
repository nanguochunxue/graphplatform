package com.haizhi.graph.search.api.es.service;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.search.api.model.qo.KeySearchQo;
import com.haizhi.graph.search.api.model.qo.NativeSearchQo;
import com.haizhi.graph.search.api.model.qo.SearchQo;
import com.haizhi.graph.search.api.model.vo.KeySearchVo;
import com.haizhi.graph.search.api.model.vo.NativeSearchVo;
import com.haizhi.graph.search.api.model.vo.SearchVo;

/**
 * Created by chengmo on 2019/4/23.
 */
public interface EsService {

    Response<SearchVo> search(SearchQo searchQo);

    Response<KeySearchVo> searchByKeys(KeySearchQo searchQo);

    Response<NativeSearchVo> searchNative(NativeSearchQo searchQo);

    Response<Object> executeProxy(String request);
}
