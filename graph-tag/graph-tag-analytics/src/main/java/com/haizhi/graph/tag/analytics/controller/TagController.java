package com.haizhi.graph.tag.analytics.controller;

import com.haizhi.graph.common.bean.PageResult;
import com.haizhi.graph.common.bean.Result;
import com.haizhi.graph.common.model.v0.PageResponse;
import com.haizhi.graph.common.model.v0.Response;
import com.haizhi.graph.common.web.constant.MediaType;
import com.haizhi.graph.tag.analytics.model.*;
import com.haizhi.graph.tag.analytics.service.TagPersistService;
import com.haizhi.graph.tag.analytics.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by chengmo on 2018/3/13.
 */
@Api(description = "[标签]增删改查")
@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService tagService;
    @Autowired
    private TagPersistService tagPersistService;

    @ApiOperation(value = "获取多个企业标签列表", notes = "输入 {\"graph\": \"crm_dev2\",\"objectKeys\": [],\"tagIds\": []}")
    @PostMapping(path = "/findByObjectKeys", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response findByObjectKeys(@RequestBody TagQo params){
        Result result = tagService.findByObjectKeys(params);
        return Response.get(result);
    }

    @ApiOperation(value = "分页查询标签", notes = "输入 {\"graph\": \"crm_dev2\",\"tagStatuses\": [\"UP\"]}")
    @PostMapping(path = "/findPage", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public PageResponse findPage(@RequestBody TagPageQo params){
        PageResult result = tagService.findPage(params);
        return PageResponse.get(result);
    }

    @ApiOperation(value = "分页查询标签逻辑参数", notes = "输入 {\"graph\": \"crm_dev2\",\"type\": \"SQL\",\"name\": \"\"}")
    @PostMapping(path = "/findPageTagParameters", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public PageResponse findPageTagParameters(@RequestBody TagParameterQo params){
        PageResult result = tagService.findPageTagParameters(params);
        return PageResponse.get(result);
    }

    @ApiOperation(value = "根据标签状态获取标签配置信息", notes = "无")
    @PostMapping(path = "/findByTagStatus", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response findByTagStatus(@RequestBody TagReq params){
        Result result = tagService.findByTagStatus(params);
        return Response.get(result);
    }

    @ApiOperation(value = "更新标签状态", notes = "输入 {\"tagId\": 1,\"tagStatus\": \"UP\"} " +
            "CREATED-已创建,APPLIED-已申请,UP-已上架,REJECTED-已驳回,DOWN-已下架")
    @PostMapping(path = "/updateTagStatus", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response updateTagStatus(@RequestBody TagReq params){
        Result result = tagService.updateTagStatus(params);
        return Response.get(result);
    }

    @ApiOperation(value = "申请或审批管理", notes = "输入 {\"tagId\": 1001,\"operation\": \"APPLY\", \"operateBy\": \"user01\"} " +
            "APPLY-申请上架,CANCEL_APPLY-取消申请,UP-上架,DOWN-下架")
    @PostMapping(path = "/applyOrApproveMgt", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response applyOrApproveMgt(@RequestBody TagMgtReq params){
        Result result = tagService.applyOrApproveMgt(params);
        return Response.get(result);
    }

    @ApiOperation(value = "获取单个标签配置", notes = "输入 {\"graph\": \"crm_dev2\",\"tagId\": 1}")
    @PostMapping(path = "/get", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response get(@RequestBody TagReq params){
        Result result = tagService.get(params);
        return Response.get(result);
    }

    @ApiOperation(value = "保存标签配置", notes = "待补充")
    @PostMapping(path = "/save", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response save(@RequestBody TagReq params){
        Result result = tagService.insert(params);
        return Response.get(result);
    }

    @ApiOperation(value = "修改标签配置", notes = "待补充")
    @PostMapping(path = "/update", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response update(@RequestBody TagReq params){
        Result result = tagService.update(params);
        return Response.get(result);
    }

    @ApiOperation(value = "删除标签", notes = "待补充")
    @PostMapping(path = "/delete", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response delete(@RequestBody TagReq params){
        Result result = tagService.delete(params);
        return Response.get(result);
    }

    @ApiOperation(value = "同步标签配置信息到ES", notes = "输入 {\"graph\": \"crm_dev2\"}")
    @PostMapping(path = "/syncDataToEs", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response syncDataToEs(@RequestBody TagReq params){
        return tagPersistService.syncDataToEs(params.getGraph());
    }
}
