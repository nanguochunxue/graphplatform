package com.haizhi.graph.search.api.gdb.service;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.search.api.model.qo.*;
import com.haizhi.graph.search.api.model.vo.*;

/**
 * Created by chengmo on 2018/6/5.
 */
public interface GdbService {

    Response<GdbSearchVo> searchGdb(GdbSearchQo searchQo);

    Response<GdbAtlasVo> searchAtlas(GdbAtlasQo searchQo);
    
    Response<KeySearchVo> searchByKeys(KeySearchQo searchQo);

    Response<NativeSearchVo> searchNative(NativeSearchQo searchQo);

}
