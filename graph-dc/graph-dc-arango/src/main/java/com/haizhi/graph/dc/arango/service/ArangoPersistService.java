package com.haizhi.graph.dc.arango.service;

import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;

/**
 * Created by chengmo on 2018/11/14.
 */
public interface ArangoPersistService {

    CudResponse bulkPersist(DcInboundDataSuo cuo);
}
