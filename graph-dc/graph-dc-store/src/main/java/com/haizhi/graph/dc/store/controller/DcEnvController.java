package com.haizhi.graph.dc.store.controller;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.dc.core.model.suo.DcEnvSuo;
import com.haizhi.graph.dc.core.model.vo.DcEnvDetailVo;
import com.haizhi.graph.dc.core.model.vo.DcEnvVo;
import com.haizhi.graph.dc.core.service.DcEnvService;
import com.haizhi.graph.sys.file.model.po.SysDictPo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by chengangxiong on 2019/03/25
 */
@Api(description = "[环境管理]-增删改查")
@RestController
@RequestMapping("/env")
public class DcEnvController {

    private static final GLog LOG = LogFactory.getLogger(DcEnvController.class);

    @Autowired
    private DcEnvService dcEnvService;

    @ApiOperation(value = "查询所有")
    @PostMapping(value = "/find")
    public Response<DcEnvVo> find() {
        return dcEnvService.findAll();
    }

    @ApiOperation(value = "新增环境")
    @PostMapping(value = "/save")
    public Response save(@RequestBody @Valid DcEnvSuo dcEnvSuo) {
        dcEnvService.saveOrUpdate(dcEnvSuo);
        return Response.success();
    }

    @ApiOperation(value = "更新环境信息")
    @PostMapping(value = "/update")
    public Response update(@RequestBody @Valid DcEnvSuo dcEnvSuo) {
        dcEnvService.saveOrUpdate(dcEnvSuo);
        return Response.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/delete")
    public Response delete(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        return dcEnvService.delete(id);
    }

    @ApiOperation(value = "根据Id获取详情")
    @GetMapping(value = "/findById")
    public Response<DcEnvDetailVo> findById(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        return dcEnvService.findById(id);
    }

    @ApiOperation(value = "获取版本信息")
    @GetMapping(value = "/findEnvVersion")
    public Response<List<SysDictPo>> findEnvVersion() {
        return dcEnvService.findEnvVersion();
    }

}
