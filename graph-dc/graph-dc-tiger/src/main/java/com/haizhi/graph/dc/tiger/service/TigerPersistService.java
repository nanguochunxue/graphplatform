package com.haizhi.graph.dc.tiger.service;

import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;

/**
 * Created by chengmo on 2018/11/14.
 */
public interface TigerPersistService {

    CudResponse bulkPersist(DcInboundDataSuo cuo);

}
