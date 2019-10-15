package com.haizhi.graph.common.web.controller;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by tanghaiyang on 2018/5/29.
 */
@Api(description = "[通用控制器]")
@RestController
@RequestMapping("/")
public class GenericController {

    private static final GLog LOG = LogFactory.getLogger(GenericController.class);

    @ApiOperation(value = "开启日志审计打印")
    @GetMapping(value = "/enableLogAudit")
    public Response enableLogAudit() {
        GLog.AUDIT_ENABLED = true;
        LOG.info("success to enable log audit!");
        LOG.audit("this is audit log!");
        return Response.success();
    }

    @ApiOperation(value = "关闭日志审计打印")
    @GetMapping(value = "/disableLogAudit")
    public Response disableLogAudit() {
        GLog.AUDIT_ENABLED = false;
        LOG.info("success to disable log audit!");
        LOG.audit("this is audit log!");
        return Response.success();
    }
}
