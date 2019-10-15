package com.haizhi.graph.tag.analytics.service.builder;

import com.haizhi.graph.tag.analytics.model.TagCategoryVo;
import com.haizhi.graph.tag.core.domain.TagCategory;

/**
 * Created by chengmo on 2018/7/20.
 */
public class TagCategoryVoBuilder {

    public static TagCategoryVo create(TagCategory tc){
        TagCategoryVo vo = new TagCategoryVo();
        vo.setId(tc.getId());
        vo.setGraph(tc.getGraph());
        vo.setTagCategoryName(tc.getTagCategoryName());
        vo.setTagCategoryDesc(tc.getTagCategoryDesc());
        vo.setParent(tc.getParent());
        vo.setLevel(tc.getLevel());
        vo.setLeaf(tc.getIsLeaf());
        return vo;
    }
}
