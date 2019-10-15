package com.haizhi.graph.plugins.dc.inbound.api;

import com.haizhi.graph.common.model.v1.Response;

/**
 * Created by chengmo on 2018/12/13.
 */
public interface DcInboundService {
    Response bulkInbound(String requestApi);

    Response bulkInbound(String requestApi, Object obj);
}
