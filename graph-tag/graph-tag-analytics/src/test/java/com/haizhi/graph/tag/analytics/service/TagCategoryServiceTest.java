package com.haizhi.graph.tag.analytics.service;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.bean.Result;
import com.haizhi.graph.tag.analytics.model.TagReq;
import com.haizhi.graph.tag.analytics.model.TagCategoryVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by chengmo on 2018/5/3.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class TagCategoryServiceTest {

    static final String GRAPH = "crm_dev2";

    @Autowired
    TagCategoryService tagCategoryService;

    @Test
    public void findAll(){
        List<TagCategoryVo> list = tagCategoryService.findAll(GRAPH);
        System.out.println(JSON.toJSONString(list, true));
    }

    @Test
    public void saveOrUpdate(){
        TagReq params = new TagReq();
        params.setGraph(GRAPH);
        params.setTagCategoryName("测试分类1");
        params.setTagCategoryDesc("测试分类111");
        Result result = tagCategoryService.saveOrUpdate(params);
        System.out.println(JSON.toJSONString(result, true));
    }

    @Test
    public void delete(){
        Result result = tagCategoryService.delete(8);
        System.out.println(JSON.toJSONString(result, true));
    }
}
