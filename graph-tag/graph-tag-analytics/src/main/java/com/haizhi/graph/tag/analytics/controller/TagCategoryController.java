package com.haizhi.graph.tag.analytics.controller;

import com.haizhi.graph.common.bean.Result;
import com.haizhi.graph.common.model.v0.Response;
import com.haizhi.graph.common.web.constant.MediaType;
import com.haizhi.graph.tag.analytics.model.TagCategoryVo;
import com.haizhi.graph.tag.analytics.model.TagReq;
import com.haizhi.graph.tag.analytics.service.TagCategoryService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by chengmo on 2018/3/13.
 */
@Api(description = "[标签分类]增删改查")
@RestController
@RequestMapping("/category")
public class TagCategoryController {

    @Autowired
    private TagCategoryService tagTypeService;

    @PostMapping(path = "/findAll", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response findAll(@RequestBody TagReq params){
        List<TagCategoryVo> tagCategories = tagTypeService.findAll(params.getGraph());
        return Response.success(tagCategories);
    }

    @PostMapping(path = "/saveOrUpdate", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response saveOrUpdate(@RequestBody TagReq params){
        Result result = tagTypeService.saveOrUpdate(params);
        return Response.get(result);
    }

    @PostMapping(path = "/delete", consumes = MediaType.APP_JSON, produces = MediaType.APP_JSON)
    public Response delete(@RequestBody TagReq params){
        Result result =  tagTypeService.delete(params.getTagCategoryId());
        return Response.get(result);
    }
}
