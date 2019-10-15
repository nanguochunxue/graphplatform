package com.haizhi.graph.dc.inbound.api.service;

import com.haizhi.graph.common.model.v1.Response;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;

/**
 * Created by chengmo on 2018/10/24.
 */
public interface ApiInboundService {

    Response bulkInbound(DcInboundDataSuo cuo);
}
