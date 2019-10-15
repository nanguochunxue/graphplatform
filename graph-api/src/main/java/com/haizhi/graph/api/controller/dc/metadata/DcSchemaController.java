package com.haizhi.graph.api.controller.dc.metadata;

import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.qo.DcSchemaApiQo;
import com.haizhi.graph.dc.core.model.qo.DcSchemaCheckQO;
import com.haizhi.graph.dc.core.model.qo.DcSchemaQo;
import com.haizhi.graph.dc.core.model.suo.DcSchemaSuo;
import com.haizhi.graph.dc.core.model.vo.DcNameCheckVo;
import com.haizhi.graph.dc.core.model.vo.DcSchemaVo;
import com.haizhi.graph.dc.core.service.DcSchemaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by chengangxiong on 2019/01/08
 */
@Api(description = "[元数据-表]-增删改查")
@RestController
@RequestMapping("/schema")
public class DcSchemaController {

    @Autowired
    private DcSchemaService dcSchemaService;

    @ApiOperation(value = "分页查询")
    @PostMapping(value = "/findPage")
    public PageResponse findPage(@RequestBody @Valid DcSchemaQo dcSchemaQo) {
        return dcSchemaService.findPage(dcSchemaQo);
    }

    @ApiOperation(value = "查询 - api")
    @PostMapping(value = "/find")
    public Response<List<DcSchemaVo>> find(@RequestBody @Valid DcSchemaApiQo dcSchemaApiQo) {
        return dcSchemaService.findAll(dcSchemaApiQo);
    }

    @ApiOperation(value = "查询 - api")
    @GetMapping(value = "/findAll")
    public Response<List<DcSchemaVo>> findAll(@ApiParam(value = "graphId", required = true) @RequestParam @Valid Long graphId) {
        return dcSchemaService.findAll(graphId);
    }

    @ApiOperation(value = "获取数据源配置的可用资源")
    @GetMapping(value = "/findAvailableStore")
    public Response findAvailableStore(@ApiParam(value = "graphId", required = true) @RequestParam @Valid Long graphId) {
        return dcSchemaService.findAvailableStore(graphId);
    }

    @ApiOperation(value = "表类型selector查询")
    @GetMapping(value = "/findSchemaTypeList")
    public Response<List<SchemaType>> findSchemaTypeList() {
        return dcSchemaService.findSchemaTypeList();
    }

    @ApiOperation(value = "表名称校验")
    @PostMapping(value = "/checkName")
    public Response<DcNameCheckVo> checkName(@RequestBody @Valid DcSchemaCheckQO checkQO) {
        return dcSchemaService.check(checkQO);
    }

    @ApiOperation(value = "增加或编辑")
    @PostMapping(value = "/saveOrUpdate")
    public Response saveOrUpdate(@RequestBody @Valid DcSchemaSuo dcSchemaSuo) {
        return dcSchemaService.saveOrUpdate(dcSchemaSuo);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/delete")
    public Response delete(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        return dcSchemaService.delete(id);
    }
}
