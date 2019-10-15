package com.haizhi.graph.dc.hbase.service;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.search.api.model.qo.KeySearchQo;
import com.haizhi.graph.search.api.model.vo.KeySearchVo;

/**
 * Created by chengmo on 2018/7/19.
 */
public interface HBaseQueryService {

    Response<KeySearchVo> searchByKeys(KeySearchQo searchQo);

}
