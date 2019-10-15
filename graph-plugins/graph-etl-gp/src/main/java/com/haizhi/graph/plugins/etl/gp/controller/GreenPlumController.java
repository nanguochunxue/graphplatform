package com.haizhi.graph.plugins.etl.gp.controller;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.model.plugins.etl.gp.EtlGreenPlumQo;
import com.haizhi.graph.plugins.etl.gp.service.GreenPlumService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by chengmo on 2019/4/15.
 */
@Api(description = "[GreenPlum操作]-导出")
@RestController
@RequestMapping("/")
public class GreenPlumController {

    @Autowired
    private GreenPlumService greenPlumService;

    @ApiOperation(value = "健康检查接口")
    @GetMapping(value = "")
    public Response healthCheck() {
        return Response.success("GP_SERVER_RUNNING");
    }

    @ApiOperation(value = "开始导出文件")
    @PostMapping(value = "/startExportFile")
    public Response startExportFile(@RequestBody @Valid EtlGreenPlumQo greenPlumOo) {
        return greenPlumService.startExport(greenPlumOo);
    }

    @ApiOperation(value = "获取导出进度")
    @PostMapping(value = "/getExportProgress")
    public Response getExportProgress(@RequestBody @Valid EtlGreenPlumQo greenPlumOo) {
        return greenPlumService.getExportProgress(greenPlumOo);
    }
}
