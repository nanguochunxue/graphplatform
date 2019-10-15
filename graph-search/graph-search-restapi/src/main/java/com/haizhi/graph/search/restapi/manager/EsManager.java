package com.haizhi.graph.search.restapi.manager;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.search.api.constant.EsVersion;
import com.haizhi.graph.search.api.model.qo.KeySearchQo;
import com.haizhi.graph.search.api.model.qo.NativeSearchQo;
import com.haizhi.graph.search.api.model.qo.SearchQo;
import com.haizhi.graph.search.api.model.vo.KeySearchVo;
import com.haizhi.graph.search.api.model.vo.NativeSearchVo;
import com.haizhi.graph.search.api.model.vo.SearchVo;

/**
 * Created by tanghaiyang on 2019/4/23.
 */
public interface EsManager {

    Response<SearchVo> search(SearchQo searchQo, EsVersion version);

    Response<KeySearchVo> searchByKeys(KeySearchQo searchQo, EsVersion version);

    Response<NativeSearchVo> searchNative(NativeSearchQo searchQo, EsVersion version);

    Response<Object> executeProxy(String request, EsVersion version);
}
