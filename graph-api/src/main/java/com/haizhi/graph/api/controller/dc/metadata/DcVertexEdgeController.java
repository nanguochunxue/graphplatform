package com.haizhi.graph.api.controller.dc.metadata;

import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.qo.DcVertexEdgeQo;
import com.haizhi.graph.dc.core.model.suo.DcVertexEdgeSuo;
import com.haizhi.graph.dc.core.service.DcVertexEdgeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created by chengangxiong on 2019/01/08
 */
@Api(description = "[数据中心-图顶点边数据]-增删改查")
@RestController
@RequestMapping("/vertexedge")
public class DcVertexEdgeController {

    @Autowired
    private DcVertexEdgeService dcVertexEdgeService;

    @ApiOperation(value = "查询")
    @PostMapping(value = "/findPage")
    public PageResponse findPage(@RequestBody @Valid DcVertexEdgeQo dcVertexEdgeQo) {
        return dcVertexEdgeService.findPage(dcVertexEdgeQo);
    }

    @ApiOperation(value = "查询")
    @PostMapping(value = "/find")
    public Response find(@RequestBody @Valid DcVertexEdgeQo dcVertexEdgeQo) {
        return dcVertexEdgeService.find(dcVertexEdgeQo);
    }

    @ApiOperation(value = "查询")
    @PostMapping(value = "/findCollections")
    public Response findCollections(@RequestBody @Valid DcVertexEdgeQo dcVertexEdgeQo) {
        return dcVertexEdgeService.findCollections(dcVertexEdgeQo);
    }

    @ApiOperation(value = "增加或编辑")
    @PostMapping(value = "/saveOrUpdate")
    public Response saveOrUpdate(@RequestBody @Valid DcVertexEdgeSuo dcVertexEdgeSuo) {
        return dcVertexEdgeService.saveOrUpdate(dcVertexEdgeSuo);
    }

    @ApiOperation(value = "删除")
    @PostMapping(value = "/delete")
    public Response delete(@RequestBody @Valid DcVertexEdgeSuo dcVertexEdgeSuo) {
        return dcVertexEdgeService.delete(dcVertexEdgeSuo);
    }
}
