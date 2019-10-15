package com.haizhi.graph.dc.store.controller;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.qo.DcNameCheckQO;
import com.haizhi.graph.dc.core.model.qo.DcStoreAutoUrlQo;
import com.haizhi.graph.dc.core.model.qo.DcStoreQo;
import com.haizhi.graph.dc.core.model.qo.GPExporterConnectQo;
import com.haizhi.graph.dc.core.model.suo.DcStoreSuo;
import com.haizhi.graph.dc.core.model.vo.DcStoreSelectorVo;
import com.haizhi.graph.dc.core.model.vo.DcStoreVo;
import com.haizhi.graph.dc.core.service.DcStoreService;
import com.haizhi.graph.dc.store.service.StoreManageService;
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
@Api(description = "[数据源]-增删改查")
@RestController
@RequestMapping("/store")
public class DcStoreController {

    @Autowired
    private DcStoreService dcStoreService;

    @Autowired
    private StoreManageService storeManageService;

    @ApiOperation(value = "数据源类型selector")
    @PostMapping(value = "/storeTypeList")
    public Response<List<String>> storeTypeList() {
        return dcStoreService.findStoreTypeList();
    }

    @ApiOperation(value = "分页查询")
    @PostMapping(value = "/findPage")
    public PageResponse findPage(@RequestBody @Valid DcStoreQo dcStoreQo) {
        return storeManageService.findPage(dcStoreQo);
    }

    @ApiOperation(value = "查询所有数据源地址")
    @GetMapping(value = "/findStoreList")
    public Response<List<DcStoreSelectorVo>> findAllEs(@ApiParam(value = "storeType", required = true) @RequestParam @Valid StoreType storeType) {
        return dcStoreService.findAll(storeType);
    }

    @ApiOperation(value = "名称校验")
    @PostMapping(value = "/checkName")
    public Response checkName(@RequestBody @Valid DcNameCheckQO dcNameCheckQO) {
        return dcStoreService.check(dcNameCheckQO);
    }

    @ApiOperation(value = "增加或编辑")
    @PostMapping(value = "/saveOrUpdate")
    public Response saveOrUpdate(@RequestBody @Valid DcStoreSuo dcStoreSuo) {
        return dcStoreService.saveOrUpdate(dcStoreSuo);
    }

    @ApiOperation(value = "自动获取数据源url")
    @PostMapping(value = "/autoGenUrl")
    public Response autoGenUrl(@RequestBody @Valid DcStoreAutoUrlQo dcStoreAutoUrlQo) {
        return storeManageService.autoGenUrl(dcStoreAutoUrlQo);
    }

    @ApiOperation(value = "版本列表")
    @PostMapping(value = "/supportedVersion")
    public Response supportedVersion(@ApiParam(value = "storeType", required = true) @RequestParam @Valid StoreType storeType) {
        return storeManageService.supportedVersion(storeType);
    }

    @ApiOperation(value = "连通性测试")
    @PostMapping(value = "/testConnect")
    public Response<String> testConnect(@RequestBody @Valid DcStoreSuo dcStoreSuo) {
        return storeManageService.testConnect(dcStoreSuo);
    }

    @ApiOperation(value = "连通性测试 - gp卸数服务")
    @PostMapping(value = "/gpExportConnectTest")
    public Response<String> testConnectExportServer(@RequestBody @Valid GPExporterConnectQo gpExporterConnectQo) {
        return storeManageService.testConnect(gpExporterConnectQo.getExporterUrl());
    }

    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/delete")
    public Response delete(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        return dcStoreService.delete(id);
    }

    @ApiOperation(value = "根据ID查找")
    @GetMapping(value = "/findById")
    public Response<DcStoreVo> findById(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        return dcStoreService.findById(id);
    }
}
