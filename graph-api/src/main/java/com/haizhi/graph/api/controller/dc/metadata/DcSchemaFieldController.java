package com.haizhi.graph.api.controller.dc.metadata;

import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.qo.DcAuthorizedFieldQo;
import com.haizhi.graph.dc.core.model.qo.DcSchemaFieldApiQo;
import com.haizhi.graph.dc.core.model.qo.DcSchemaFieldCheckQO;
import com.haizhi.graph.dc.core.model.qo.DcSchemaFieldMainCheckQO;
import com.haizhi.graph.dc.core.model.qo.DcSchemaFieldQo;
import com.haizhi.graph.dc.core.model.suo.DcSchemaFieldSuo;
import com.haizhi.graph.dc.core.model.vo.DcNameCheckVo;
import com.haizhi.graph.dc.core.model.vo.DcSchemaFieldVo;
import com.haizhi.graph.dc.core.service.DcSchemaFieldService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by chengangxiong on 2019/01/08
 */
@Api(description = "[元数据-表字段]-增删改查")
@RestController
@RequestMapping("/field")
public class DcSchemaFieldController {

    @Autowired
    private DcSchemaFieldService dcSchemaFieldService;

    @ApiOperation(value = "查询")
    @PostMapping(value = "/findPage")
    public PageResponse findPage(@RequestBody @Valid DcSchemaFieldQo dcSchemaFieldQo) {
        return dcSchemaFieldService.findPage(dcSchemaFieldQo);
    }

    @ApiOperation(value = "查询-获取授权字段")
    @PostMapping(value = "/findAuthorizedFieldPage")
    public PageResponse<DcSchemaFieldVo> findAuthorizedFieldPage(@RequestBody @Valid DcAuthorizedFieldQo qo) {
        return dcSchemaFieldService.findAuthorizedFieldPage(qo);
    }

    @ApiOperation(value = "模糊查询字段名")
    @GetMapping(value = "/findDistinctByFieldByLike")
    public Response<List<String>> findDistinctByFieldByLike(@RequestParam("name") @ApiParam(value = "字段英文名",required = true) String name) {
        return dcSchemaFieldService.findDistinctByFieldByLike(name);
    }


    @ApiOperation(value = "查询-api")
    @PostMapping(value = "/findAll")
    public Response<List<DcSchemaFieldVo>> findAll(@RequestBody @Valid DcSchemaFieldApiQo dcSchemaFieldApiQo) {
        return dcSchemaFieldService.findAll(dcSchemaFieldApiQo);
    }

    @ApiOperation(value = "表字段名称校验")
    @PostMapping(value = "/checkName")
    public Response<DcNameCheckVo> checkName(@RequestBody @Valid DcSchemaFieldCheckQO checkQO) {
        return dcSchemaFieldService.check(checkQO);
    }

    @ApiOperation(value = "是否已经存在主字段")
    @PostMapping(value = "/checkHasMainField")
    public Response<Boolean> checkHasMainField(@RequestBody @Valid DcSchemaFieldMainCheckQO checkQO) {
        return dcSchemaFieldService.checkHasMainField(checkQO);
    }

    @ApiOperation(value = "增加或编辑")
    @PostMapping(value = "/saveOrUpdate")
    public Response saveOrUpdate(@RequestBody @Valid DcSchemaFieldSuo dcSchemaFieldSuo) {
        return dcSchemaFieldService.saveOrUpdate(dcSchemaFieldSuo);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/delete")
    public Response delete(@ApiParam(value = "id", required = true) @RequestParam @Valid Long id) {
        return dcSchemaFieldService.delete(id);
    }
}
