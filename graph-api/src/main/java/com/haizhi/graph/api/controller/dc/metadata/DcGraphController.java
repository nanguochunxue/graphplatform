package com.haizhi.graph.api.controller.dc.metadata;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.qo.DcGraphCheckQO;
import com.haizhi.graph.dc.core.model.qo.DcGraphIdsQo;
import com.haizhi.graph.dc.core.model.suo.DcGraphSuo;
import com.haizhi.graph.dc.core.model.vo.DcGraphDetailVo;
import com.haizhi.graph.dc.core.model.vo.DcGraphFrameVo;
import com.haizhi.graph.dc.core.model.vo.DcGraphVo;
import com.haizhi.graph.dc.core.model.vo.DcNameCheckVo;
import com.haizhi.graph.dc.core.service.DcGraphService;
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
@Api(description = "[元数据-资源库]-增删改查")
@RestController
@RequestMapping("/graph")
public class DcGraphController {

    @Autowired
    private DcGraphService dcGraphService;

    @ApiOperation(value = "查询")
    @PostMapping(value = "/find")
    public Response find() {
        return dcGraphService.findAll();
    }

    @ApiOperation(value = "查询-api")
    @PostMapping(value = "/findByIds")
    public Response<DcGraphVo> findByIds(@RequestBody @Valid DcGraphIdsQo ids) {
        return dcGraphService.findByIds(ids);
    }

    @ApiOperation(value = "根据ID查找")
    @GetMapping(value = "/findById")
    public Response<DcGraphDetailVo> findById(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        return dcGraphService.findDetailById(id);
    }

    @ApiOperation(value = "查询")
    @PostMapping(value = {"/findGraphFrame", "/findTaskGraph"})
    public Response<List<DcGraphFrameVo>> findGraphFrame() {
        return dcGraphService.findGraphFrame();
    }

    @ApiOperation(value = "增加或编辑")
    @PostMapping(value = "/saveOrUpdate")
    public Response saveOrUpdate(@RequestBody @Valid DcGraphSuo dcGraphSuo) {
        return dcGraphService.saveOrUpdate(dcGraphSuo);
    }

    @ApiOperation(value = "资源库名称校验")
    @PostMapping(value = "/checkName")
    public Response<DcNameCheckVo> saveOrUpdate(@RequestBody @Valid DcGraphCheckQO dcNameCheckQO) {
        return dcGraphService.check(dcNameCheckQO);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/delete")
    public Response delete(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        return dcGraphService.delete(id);
    }
}
