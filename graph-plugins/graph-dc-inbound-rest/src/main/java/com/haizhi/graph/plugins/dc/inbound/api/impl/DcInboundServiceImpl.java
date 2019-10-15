package com.haizhi.graph.plugins.dc.inbound.api.impl;

import com.haizhi.graph.common.model.v1.Response;
import com.haizhi.graph.common.rest.RestService;
import com.haizhi.graph.common.rest.RestFactory;
import com.haizhi.graph.plugins.dc.inbound.api.DcInboundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chengmo on 2018/12/13.
 */
public class DcInboundServiceImpl implements DcInboundService {

    private static final Logger logger = LoggerFactory.getLogger(DcInboundServiceImpl.class);

    private RestService restService;

    public DcInboundServiceImpl() {
        restService = RestFactory.getRestService();
    }

    @Override
    public Response bulkInbound(String requestApi) {
        return new Response();
    }

    @Override
    public Response bulkInbound(String requestApi, Object obj) {
        try {
            return restService.doPost(requestApi, obj, Response.class);
        } catch (Exception e) {
            logger.warn("call rest post error", e);
        }
        return null;
    }
}
