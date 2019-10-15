package com.haizhi.graph.dc.common.service;

import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;

/**
 * Created by chengmo on 2018/5/14.
 */
public interface HBasePersistService {

    CudResponse bulkPersist(DcInboundDataSuo cuo);
}
