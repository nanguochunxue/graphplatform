package com.haizhi.graph.dc.inbound.api.controller;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.v1.Response;
import com.haizhi.graph.common.web.constant.MediaType;
import com.haizhi.graph.dc.core.constant.DcConstants;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.inbound.api.service.ApiInboundService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by chengmo on 2018/10/24.
 */
@Api(description = "[数据中心-数据接入]-API方式接入")
@RestController
@RequestMapping("/api")
public class ApiInboundController {

    private static final GLog LOG = LogFactory.getLogger(ApiInboundController.class);

    @Autowired
    private ApiInboundService apiInboundService;

    @ApiOperation(value = "单表批量数据接入", notes = "")
    @PostMapping(path = "/bulk", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response bulkInbound(@RequestBody DcInboundDataSuo suo) {
        LOG.audit("taskInstanceId={0}, size={1}",
                suo.getHeaderOptions().getString(DcConstants.KEY_TASK_INSTANCE_ID),
                suo.getRowsSize());
        return apiInboundService.bulkInbound(suo);
    }
}