package com.haizhi.graph.tag.analytics.service.builder;

import com.haizhi.graph.tag.analytics.model.TagReq;
import com.haizhi.graph.tag.core.domain.Tag;

/**
 * Created by chengmo on 2018/5/4.
 */
public class TagBuilder {

    public static Tag create(TagReq params){
        Tag tag = new Tag();
        tag.setId(params.getTagId());
        tag.setGraph(params.getGraph());
        tag.setTagCategoryId(params.getTagCategoryId());
        tag.setTagName(params.getTagName());
        tag.setTagDesc(params.getTagDesc());
        tag.setTagGroup(params.getTagGroup());
        tag.setApplyBy(params.getUserId());
        return tag;
    }

    public static void update(Tag newTag, Tag tagDB){
        tagDB.setTagCategoryId(newTag.getTagCategoryId());
        tagDB.setTagName(newTag.getTagName());
        tagDB.setTagDesc(newTag.getTagDesc());
        tagDB.setOriginalRule(newTag.getOriginalRule());
        tagDB.setRule(newTag.getRule());
        tagDB.setRuleScript(newTag.getRuleScript());
    }
}
