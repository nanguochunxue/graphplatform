package com.haizhi.graph.tag.analytics.service;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.bean.PageResult;
import com.haizhi.graph.common.bean.Result;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.tag.analytics.model.*;
import com.haizhi.graph.tag.core.dao.TagDAO;
import com.haizhi.graph.tag.core.domain.Tag;
import com.haizhi.graph.tag.core.domain.TagParameterType;
import com.haizhi.graph.tag.core.domain.TagStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengmo on 2018/5/3.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class TagServiceTest {

    static final String GRAPH = "crm_dev2";

    @Autowired
    TagService tagService;
    @Autowired
    TagDAO tagDAO;

    @Test
    public void findByObjectKey(){
        TagQo params = new TagQo();
        params.setGraph(GRAPH);
        params.addObjectKeys("7908891a00d02b29354c4dd5147de439");
        //params.addTagIds(1001L, 70500101L);
        Result result = tagService.findByObjectKeys(params);
        JSONUtils.println(result);
    }

    @Test
    public void findByObjectKeys(){
        TagQo params = new TagQo();
        params.setGraph(GRAPH);
        params.addObjectKeys("7908891a00d02b29354c4dd5147de439",
                "BD62C752D4781FBE5532040C6FC8FFA9");
        params.addTagIds(1001L, 70500101L);
        Result result = tagService.findByObjectKeys(params);
        JSONUtils.println(result);
    }

    @Test
    public void findPage(){
        TagPageQo tagPageQo = new TagPageQo();
        tagPageQo.setGraph("crm_dev2");
        tagPageQo.setPageNo(1);
        tagPageQo.setPageSize(10);
        tagPageQo.setTagCategoryIds("1");
        //tagPageQo.setTagGroup(TagGroup.SYSTEM);
        //tagPageQo.setTagCategoryIds("");
        tagPageQo.setUpTime("2018-08-03 10:38:38|2018-08-10 10:38:38");
        //tagPageQo.setKeyword("");
        PageResult pageResult = tagService.findPage(tagPageQo);
        JSONUtils.println(pageResult);
    }

    @Test
    public void findPageTagParameters(){
        TagParameterQo qo = new TagParameterQo();
        qo.setGraph(GRAPH);
        qo.setType(TagParameterType.SQL);
        qo.setName("理财");
        qo.setReference("");
        PageResult pageResult = tagService.findPageTagParameters(qo);
        JSONUtils.println(pageResult);
    }

    @Test
    public void findByTagStatus(){
        TagReq params = new TagReq();
        params.setGraph(GRAPH);
        params.setTagStatus(TagStatus.CREATED);
        Result result = tagService.findByTagStatus(params);
        JSONUtils.println(result);
    }

    @Test
    public void updateTagStatus(){
        TagReq params = new TagReq();
        params.setTagId(401001);
        params.setTagStatus(TagStatus.UP);
        Result result = tagService.updateTagStatus(params);
        JSONUtils.println(result);
    }

    @Test
    public void applyOrApproveMgt(){
        TagMgtReq params = new TagMgtReq();
        params.setTagId(1000000003);
        params.setOperation(TagMgtReq.Operation.APPLY);
        params.setOperateBy("testUser");
        Result result = tagService.applyOrApproveMgt(params);
        JSONUtils.println(result);
    }

    @Test
    public void get(){
        TagReq params = new TagReq();
        params.setGraph(GRAPH);
        params.setTagId(1000000028);
        Result result = tagService.get(params);
        JSONUtils.println(result);
    }

    @Test
    public void insert(){
        TagReq params = this.getTagParams("TagReq_insert1.json");
        Result result = tagService.insert(params);
        JSONUtils.println(result);
    }

    @Test
    public void insertSimple(){
        TagReq params = this.getTagParams("TagReq_simple_insert.json");
        Result result = tagService.insert(params);
        JSONUtils.println(result);
    }

    @Test
    public void update(){
        TagReq params = this.getTagParams("TagReq_update1.json");
        Result result = tagService.update(params);
        JSONUtils.println(result);
    }

    @Test
    public void delete(){
        TagReq params = new TagReq();
        params.setGraph(GRAPH);
        params.setTagId(70100101);
        Result result = tagService.delete(params);
        JSONUtils.println(result);
    }

    @Test
    public void saveTag(){
        Tag params = FileUtils.readJSONObject("Tag.json", Tag.class);
        tagDAO.save(params);
    }

    @Test
    public void deleteTag(){
        Tag params = FileUtils.readJSONObject("Tag.json", Tag.class);
        tagDAO.delete(params.getId());
    }

    private TagReq getTagParams(String apiName){
        String path = this.getClass().getResource("/" + apiName).getPath();
        String json = FileUtils.readTxt(path);
        return JSON.parseObject(json, TagReq.class);
    }
}
