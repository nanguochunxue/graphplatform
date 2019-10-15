package com.haizhi.graph.dc.core.service;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.po.DcGraphStorePo;
import com.haizhi.graph.dc.core.model.suo.DcGraphStoreSuo;

import java.util.Map;

/**
 * Created by chengangxiong on 2019/01/03
 */
public interface DcGraphStoreService {

    Map<StoreType, DcGraphStorePo> findByGraph(String graph);

    Response saveOrUpdate(DcGraphStoreSuo dcGraphStoreSuo);

    DcGraphStorePo findByGraphAndStoreIdAndStoreType(String graph, Long storeId, StoreType storeType);

    DcGraphStorePo findByGraphAndStoreType(String graph, StoreType storeType);
}
