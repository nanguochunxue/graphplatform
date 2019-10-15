package com.haizhi.graph.tag.analytics.controller;

import com.haizhi.graph.common.bean.Result;
import com.haizhi.graph.common.model.v0.Response;
import com.haizhi.graph.common.web.constant.MediaType;
import com.haizhi.graph.tag.analytics.model.TagValueReq;
import com.haizhi.graph.tag.analytics.service.TagValueService;
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
@Api(description = "[标签值]批量插入或更新")
@RestController
@RequestMapping("/tag/value")
public class TagValueController {

    @Autowired
    private TagValueService tagValueService;

    @ApiOperation(value = "导入名单方式批量更新标签值", notes = "输入 {\"graph\": \"crm_dev2\", \"tagId\": 1002, \"objectKeys\": [\"18e032203a060ff1a53eb39e636892d8\"]}")
    @PostMapping(path = "/bulkUpsert", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response bulkUpsert(@RequestBody TagValueReq req){
        Result result = tagValueService.bulkUpsert(req);
        return Response.get(result);
    }
}
