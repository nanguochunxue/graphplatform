package com.haizhi.graph.dc.es.service;

import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;

/**
 * Created by chengmo on 2018/11/14.
 */
public interface EsPersistService {

    CudResponse bulkPersist(DcInboundDataSuo cuo);
}
