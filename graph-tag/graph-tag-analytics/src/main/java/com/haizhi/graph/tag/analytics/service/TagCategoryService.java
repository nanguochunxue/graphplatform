package com.haizhi.graph.tag.analytics.service;

import com.haizhi.graph.common.bean.Result;
import com.haizhi.graph.tag.analytics.model.TagCategoryVo;
import com.haizhi.graph.tag.analytics.model.TagReq;
import com.haizhi.graph.tag.core.domain.TagCategory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chengmo on 2018/5/3.
 */
public interface TagCategoryService {

    List<TagCategoryVo> findAll(String graph);

    List<TagCategory> findAllCategories(String graph);

    Result saveOrUpdate(TagReq params);

    Result delete(long tagCategoryId);

    TagCategoryVo getSingleTree(List<TagCategory> list);

    Map<Long, TagCategory> getCategoryMap(String graph);

    List<TagCategory> getParentList(String graph, Long catId);

    List<TagCategory> getParentList(Long catId, Map<Long, TagCategory> map);

    Set<Long> getChildrenCatIds(String graph, Set<Long> catIds);
}
