package com.haizhi.graph.search.api.hbase.service;

import com.haizhi.graph.search.api.model.qo.KeySearchQo;
import com.haizhi.graph.search.api.model.qo.NativeSearchQo;
import com.haizhi.graph.search.api.model.vo.KeySearchVo;
import com.haizhi.graph.search.api.model.vo.NativeSearchVo;

/**
 * Created by chengmo on 2019/4/23.
 */
public interface HBaseService {

    KeySearchVo searchByKeys(KeySearchQo searchQo);

    NativeSearchVo searchNative(NativeSearchQo searchQo);
}
