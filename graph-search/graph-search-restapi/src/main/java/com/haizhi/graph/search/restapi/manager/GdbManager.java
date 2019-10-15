package com.haizhi.graph.search.restapi.manager;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.search.api.constant.GdbVersion;
import com.haizhi.graph.search.api.model.qo.*;
import com.haizhi.graph.search.api.model.vo.*;

/**
 * Created by chengmo on 2019/4/23.
 */
public interface GdbManager {

    Response<SearchVo> search(SearchQo searchQo, GdbVersion version);

    Response<GdbSearchVo> searchGdb(GdbSearchQo searchQo, GdbVersion version);

    Response<GdbAtlasVo> searchAtlas(GdbAtlasQo searchQo, GdbVersion version);

    Response<KeySearchVo> searchByKeys(KeySearchQo searchQo, GdbVersion version);

    Response<NativeSearchVo> searchNative(NativeSearchQo searchQo, GdbVersion version);
}
