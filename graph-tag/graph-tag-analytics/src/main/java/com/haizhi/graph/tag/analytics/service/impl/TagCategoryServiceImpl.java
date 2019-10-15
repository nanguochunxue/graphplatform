package com.haizhi.graph.tag.analytics.service.impl;

import com.haizhi.graph.common.bean.Result;
import com.haizhi.graph.common.constant.Constants;
import com.haizhi.graph.tag.analytics.model.TagCategoryVo;
import com.haizhi.graph.tag.analytics.model.TagReq;
import com.haizhi.graph.tag.analytics.service.TagCategoryService;
import com.haizhi.graph.tag.analytics.service.builder.TagCategoryVoBuilder;
import com.haizhi.graph.tag.core.dao.TagCategoryDAO;
import com.haizhi.graph.tag.core.domain.TagCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by chengmo on 2018/5/3.
 */
@Service
public class TagCategoryServiceImpl implements TagCategoryService {

    @Autowired
    private TagCategoryDAO tagCategoryDAO;

    @Override
    public List<TagCategoryVo> findAll(String graph) {
        List<TagCategory> list = tagCategoryDAO.findAll(graph);
        return this.getTreeList(list);
    }

    @Override
    public List<TagCategory> findAllCategories(String graph) {
        return tagCategoryDAO.findAll(graph);
    }

    @Override
    public Result saveOrUpdate(TagReq params) {
        Result result = new Result();
        TagCategory tc = new TagCategory();
        tc.setId(params.getTagCategoryId());
        tc.setParent(params.getParentId());
        tc.setGraph(params.getGraph());
        tc.setTagCategoryName(params.getTagCategoryName());
        tc.setTagCategoryDesc(params.getTagCategoryDesc());
        try {
            // insert
            if (tc.getId() == 0) {
                tagCategoryDAO.save(tc);
            }
            // update
            else {
                TagCategory tagDB = tagCategoryDAO.findOne(tc.getId());
                tagDB.setParent(tc.getParent());
                tagDB.setTagCategoryName(tc.getTagCategoryName());
                tagDB.setTagCategoryDesc(tc.getTagCategoryDesc());
                tagDB.setUpdateById(tc.getUpdateById());
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public Result delete(long tagCategoryId) {
        Result result = new Result();
        try {
            // update
            TagCategory tagDB = tagCategoryDAO.findOne(tagCategoryId);
            tagDB.setUpdatedDt(new Date());
            tagDB.setEnabledFlag(Constants.N);
            tagCategoryDAO.save(tagDB);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public TagCategoryVo getSingleTree(List<TagCategory> list){
        List<TagCategoryVo> treeList = getTreeList(list);
        if (treeList.isEmpty()){
            return null;
        }
        return treeList.iterator().next();
    }

    @Override
    public Map<Long, TagCategory> getCategoryMap(String graph){
        return this.findAllCategories(graph).stream()
                .collect(Collectors.toMap(TagCategory::getId, Function.identity()));
    }

    @Override
    public List<TagCategory> getParentList(String graph, Long catId) {
        List<TagCategory> list = tagCategoryDAO.findAll(graph);
        Map<Long, TagCategory> map = list.stream().collect(
                Collectors.toMap(TagCategory::getId, Function.identity()));
        return getParentList(catId, map);
    }

    @Override
    public List<TagCategory> getParentList(Long catId, Map<Long, TagCategory> map){
        TagCategory tc = map.get(catId);
        if (Objects.isNull(tc)){
            return Collections.emptyList();
        }
        List<TagCategory> resultList = new ArrayList<>();
        resultList.add(tc);
        findParent(tc, map, resultList);
        Collections.reverse(resultList);
        return resultList;
    }

    @Override
    public Set<Long> getChildrenCatIds(String graph, Set<Long> catIds){
        if (CollectionUtils.isEmpty(catIds)){
            return Collections.emptySet();
        }
        List<TagCategory> list = tagCategoryDAO.findAll(graph);
        List<TagCategoryVo> treeList = getTreeList(list);
        Set<Long> resultSet = new HashSet<>();
        for (Long catId : catIds) {
            for (TagCategoryVo vo : treeList) {
                if (catId != vo.getId()){
                    continue;
                }
                resultSet.add(catId);
                getChildrenCatIds(vo, resultSet);
                break;
            }
        }

        return resultSet;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private void getChildrenCatIds(TagCategoryVo vo, Set<Long> resultSet){
        if (Objects.isNull(vo.getChildren())){
            return;
        }
        for (TagCategoryVo subVo : vo.getChildren()) {
            resultSet.add(subVo.getId());
        }
    }

    private List<TagCategoryVo> getTreeList(List<TagCategory> list){
        List<TagCategoryVo> treeList = new ArrayList<>();
        for (TagCategory tc : list) {
            if (tc.getParent() != 0) {
                continue;
            }
            TagCategoryVo tree = TagCategoryVoBuilder.create(tc);
            treeList.add(findChildren(tree, list));
        }
        return treeList;
    }

    private void findParent(TagCategory tc, Map<Long, TagCategory> map, List<TagCategory> result){
        Long parent = tc.getParent();
        if (parent >= 0){
            TagCategory parentTag = map.get(parent);
            if (Objects.isNull(parentTag)){
                return;
            }
            result.add(parentTag);
            findParent(parentTag, map, result);
        }
    }

    private TagCategoryVo findChildren(TagCategoryVo tree, List<TagCategory> list){
        List<TagCategoryVo> children = new ArrayList<>();
        for (TagCategory tcc : list) {
            if (tcc.getParent() == tree.getId()) {
                TagCategoryVo node = TagCategoryVoBuilder.create(tcc);
                children.add(findChildren(node, list));
            }
        }
        tree.setChildren(children);
        return tree;
    }
}
