//package com.haizhi.graph.tag.analytics.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.TypeReference;
//import com.github.wenhao.jpa.Specifications;
//import com.google.common.collect.Sets;
//import com.haizhi.graph.common.bean.PageResult;
//import com.haizhi.graph.common.bean.Result;
//import com.haizhi.graph.common.constant.Constants;
//import com.haizhi.graph.common.core.util.PageRequestBuilder;
//import com.haizhi.graph.common.log.GLog;
//import com.haizhi.graph.common.log.LogFactory;
//import com.haizhi.graph.common.model.v0.PageQo;
//import com.haizhi.graph.common.util.DateUtils;
//import com.haizhi.graph.common.util.Getter;
//import com.haizhi.graph.engine.base.rule.logic.LogicExpress;
//import com.haizhi.graph.engine.base.rule.script.RuleScript;
//import com.haizhi.graph.engine.base.rule.script.ScriptContext;
//import com.haizhi.graph.server.es.search.vo.EsQuery;
//import com.haizhi.graph.server.es.search.vo.EsQueryResult;
//import com.haizhi.graph.server.es.search.vo.EsSearchDao;
//import com.haizhi.graph.server.es.search.query.TermFilter;
//import com.haizhi.graph.tag.analytics.bean.TagRule;
//import com.haizhi.graph.tag.analytics.bean.TagValue;
//import com.haizhi.graph.tag.analytics.model.*;
//import com.haizhi.graph.tag.analytics.service.TagCategoryService;
//import com.haizhi.graph.tag.analytics.service.TagPersistService;
//import com.haizhi.graph.tag.analytics.service.TagService;
//import com.haizhi.graph.tag.analytics.service.builder.*;
//import com.haizhi.graph.tag.analytics.util.SqlUtils;
//import com.haizhi.graph.tag.analytics.util.TagRuleParser;
//import com.haizhi.graph.tag.analytics.util.TagUtils;
//import com.haizhi.graph.tag.core.dao.*;
//import com.haizhi.graph.tag.core.domain.*;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.math.NumberUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import java.text.MessageFormat;
//import java.util.*;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
///**
// * Created by chengmo on 2018/5/3.
// */
//@Service
//public class TagServiceImpl implements TagService {
//
//    private static final GLog LOG = LogFactory.getLogger(TagServiceImpl.class);
//
//    @Autowired
//    private TagDAO tagDAO;
//    @Autowired
//    private TagDependencyDAO tagDependencyDAO;
//    @Autowired
//    private TagParameterDAO tagParameterDAO;
//    @Autowired
//    private TagSchemaDAO tagSchemaDAO;
//    @Autowired
//    private TagCategoryDAO tagCategoryDAO;
//    @Autowired
//    private EsSearchDao esSearchDao;
//    @Autowired
//    private TagCategoryService tagCategoryService;
//    @Autowired
//    private TagPersistService tagPersistService;
//
//    @Override
//    public Result findByObjectKeys(TagQo params) {
//        Result result = new Result();
//        try {
//            Set<String> objectKeys = params.getObjectKeys();
//            if (objectKeys.isEmpty()) {
//                return result;
//            }
//            Set<Long> tagIds = params.getTagIds();
//            if (objectKeys.size() > 1 && tagIds.isEmpty()) {
//                LOG.warn("tagIds is empty when the objectKeys.size > 1");
//                return result;
//            }
//            String graph = params.getGraph();
//            String tagStatus = TagStatus.UP.name();
//            Map<Long, Tag> tags = tagDAO.findByTagStatus(graph, tagStatus).stream()
//                    .collect(Collectors.toMap(Tag::getId, Function.identity()));
//            if (tags.isEmpty()) {
//                return result;
//            }
//            // EsQuery
//            EsQuery esQuery = new EsQuery(TagUtils.getTagEsIndex(graph));
//            //esQuery.setDebugEnabled(true);
//            esQuery.addType(TagValue._schema);
//            esQuery.setPageSize(1000);
//            esQuery.addHitFields(TagValue.tagId, TagValue.objectKey, TagValue.value);
//            esQuery.addTermFilter(TagValue.objectKey).addValues(objectKeys.toArray());
//            esQuery.addTermFilter(TagValue.dataType).addValues(DataType.LIST.name()).setMustNot(true);
//            esQuery.addTermFilter(TagValue.tagId).addValues(params.getTagIds().toArray());
//            EsQueryResult esResult = esSearchDao.search(esQuery);
//            if (!esResult.hasRows()) {
//                return result;
//            }
//
//            // result
//            Map<String, List<TagVo>> resultMap = new HashMap<>();
//            //List<TagVo> tagVoList = new ArrayList<>();
//            for (Map<String, Object> row : esResult.getRows()) {
//                String objectKey = Getter.get(TagValue.objectKey, row);
//                List<TagVo> tagVoList = resultMap.get(objectKey);
//                if (tagVoList == null) {
//                    tagVoList = new ArrayList<>();
//                    resultMap.put(objectKey, tagVoList);
//                }
//                // TagVo
//                long tagId = NumberUtils.toLong(Getter.get(TagValue.tagId, row));
//                Tag tag = tags.get(tagId);
//                if (tag == null || tag.getTagGroup() == TagGroup.INTERNAL) {
//                    continue;
//                }
//                TagVo tagVo = TagVoBuilder.create(tag);
//                tagVo.setTagValue(row.get(TagValue.value));
//                tagVoList.add(tagVo);
//            }
//            result.setData(resultMap);
//        } catch (Exception e) {
//            result.setMessage(e.getMessage());
//            LOG.error(e);
//        }
//        return result;
//    }
//
//    @Override
//    public PageResult findPage(TagPageQo params) {
//        PageResult pageResult = new PageResult(params.getPageNo(), params.getPageSize());
//        String graph = params.getGraph();
//        try {
//            /* 1.Get tags page */
//            Page<Tag> page = this.findPageTags(params);
//            pageResult.setTotal(page.getTotalElements());
//
//            Map<Long, TagCategory> catMap = tagCategoryService.getCategoryMap(graph);
//
//            /* 2.Add tag logic parameters */
//            List<TagVo> tagVOList = this.mergeTagParameters(page.getContent(), graph);
//            pageResult.setData(tagVOList);
//
//            /* 3.Tag results aggregation */
//            Set<Long> tagIds = new HashSet<>();
//            for (TagVo tagVo : tagVOList) {
//                tagIds.add(tagVo.getTagId());
//                List<TagCategory> catList = tagCategoryService.getParentList(tagVo.getTagCategoryId(), catMap);
//                String tagCategoryName = TagCatNamesBuilder.get(catList);
//                tagVo.setTagCategoryName(tagCategoryName);
//            }
//            Map<String, Long> tagIdToCount = queryObjectKeyCount(graph, tagIds);
//            for (TagVo tagVo : tagVOList) {
//                long tagId = tagVo.getTagId();
//                Long tagIdCount = tagIdToCount.get(tagId + "");
//                if (tagIdCount == null) {
//                    continue;
//                }
//                tagVo.setObjectKeyCount(tagIdCount);
//            }
//        } catch (Exception e) {
//            pageResult.setMessage(e.getMessage());
//            LOG.error(e);
//        }
//        return pageResult;
//    }
//
//    private Map<String, Long> queryObjectKeyCount(String graph, Set<Long> tagIds) {
//        EsQuery esQuery = new EsQuery(TagUtils.getTagEsIndex(graph));
//        esQuery.addType(TagValue._schema);
//        esQuery.setPageSize(0);
//        TermFilter tf = esQuery.addTermFilter(TagValue.tagId);
//        tf.addValues(tagIds.toArray());
//        esQuery.addTermAggregation(TagValue.tagId);
//        EsQueryResult esResult = esSearchDao.search(esQuery);
//        Map<String, Object> aggData = esResult.getAggData();
//        if (CollectionUtils.isEmpty(aggData)){
//            return Collections.EMPTY_MAP;
//        }
//        List<Map<String, Object>> rows = Getter.getListMap(aggData.get(TagValue.tagId));
//        if (CollectionUtils.isEmpty(rows)){
//            return Collections.EMPTY_MAP;
//        }
//        Map<String, Long> tagIdToCount = new HashMap<>();
//        for (Map<String, Object> row : rows) {
//            String tagId = Getter.get(TagValue.tagId, row);
//            long count = (long) row.get("count");
//            tagIdToCount.put(tagId, count);
//        }
//        return tagIdToCount;
//    }
//
//    @Override
//    public PageResult findPageTagParameters(TagParameterQo params) {
//        PageResult pageResult = new PageResult(params.getPageNo(), params.getPageSize());
//        String graph = params.getGraph();
//        try {
//            // specification
//            TagParameterType type = params.getType();
//            String name = params.getName();
//            String reference = params.getReference();
//            Specification<TagParameter> specification = Specifications.<TagParameter>and()
//                    .eq("graph", graph)
//                    .eq(Objects.nonNull(type), "type", type)
//                    .like(StringUtils.isNotBlank(name), "name", "%" + name + "%")
//                    .like(StringUtils.isNotBlank(reference), "reference", "%" + reference + "%")
//                    .build();
//            PageRequest pageRequest = PageRequestBuilder.create(params);
//            Page<TagParameter> page = tagParameterDAO.findAll(specification, pageRequest);
//            pageResult.setTotal(page.getTotalElements());
//            pageResult.setData(page.getContent());
//        } catch (Exception e) {
//            pageResult.setMessage(e.getMessage());
//            LOG.error(e);
//        }
//        return pageResult;
//    }
//
//    @Override
//    public Result findByTagStatus(TagReq params) {
//        Result result = new Result();
//        try {
//            String tagStatus = params.getTagStatus().name();
//            List<Tag> tags = tagDAO.findByTagStatus(params.getGraph(), tagStatus);
//            List<TagVo> tagVoList = new ArrayList<>();
//            for (Tag tag : tags) {
//                tagVoList.add(TagVoBuilder.create(tag));
//            }
//            result.setData(tagVoList);
//        } catch (Exception e) {
//            result.setMessage(e.getMessage());
//            LOG.error(e);
//        }
//        return result;
//    }
//
//    @Override
//    public Result updateTagStatus(TagReq params) {
//        Result result = new Result();
//        try {
//            long tagId = params.getTagId();
//            Tag tag = tagDAO.findOne(tagId);
//            if (tag == null) {
//                result.setSuccess(false);
//                result.setMessage("tagId=" + tagId + " not found");
//                return result;
//            }
//            TagStatus tagStatus = params.getTagStatus();
//            if (tagStatus != null) {
//                tag.setTagStatus(tagStatus);
//                tag.setTagStatusBak(tagStatus);
//            }
//            tagDAO.save(tag);
//            this.syncUpdateEs(tag.getGraph(), tag.getId());
//        } catch (Exception e) {
//            result.setSuccess(false);
//            result.setMessage(e.getMessage());
//            LOG.error(e);
//        }
//        return result;
//    }
//
//    @Override
//    public Result applyOrApproveMgt(TagMgtReq params) {
//        Result result = new Result();
//        try {
//            long tagId = params.getTagId();
//            Tag tag = tagDAO.findOne(tagId);
//            if (tag == null) {
//                result.setSuccess(false);
//                result.setMessage("tagId=" + tagId + " not found");
//                return result;
//            }
//            TagMgtReq.Operation operation = params.getOperation();
//            if (operation == null) {
//                result.setSuccess(false);
//                result.setMessage("operation must not be null");
//                return result;
//            }
//            switch (operation) {
//                case APPLY:
//                    tag.setTagStatus(TagStatus.APPLIED);
//                    //tag.setApplyBy(params.getOperateBy());
//                    tag.setApplyUpTime(new Date());
//                    break;
//                case CANCEL_APPLY:
//                    tag.setTagStatus(tag.getTagStatusBak());
//                    //tag.setApplyBy(params.getOperateBy());
//                    break;
//                case REJECT:
//                    tag.setTagStatus(TagStatus.REJECTED);
//                    tag.setTagStatusBak(TagStatus.REJECTED);
//                    tag.setApproveBy(params.getOperateBy());
//                    break;
//                case UP:
//                    tag.setTagStatus(TagStatus.UP);
//                    tag.setTagStatusBak(TagStatus.UP);
//                    tag.setApproveBy(params.getOperateBy());
//                    tag.setUpTime(new Date());
//                    break;
//                case DOWN:
//                    tag.setTagStatus(TagStatus.DOWN);
//                    tag.setTagStatusBak(TagStatus.DOWN);
//                    break;
//            }
//            tagDAO.save(tag);
//            this.syncUpdateEs(tag.getGraph(), tag.getId());
//        } catch (Exception e) {
//            result.setSuccess(false);
//            result.setMessage(e.getMessage());
//            LOG.error(e);
//        }
//        return result;
//    }
//
//    @Override
//    public Result get(TagReq params) {
//        Result result = new Result();
//        try {
//            String graph = params.getGraph();
//            Long tagId = params.getTagId();
//            Tag tag = tagDAO.findOne(params.getTagId());
//            if (Objects.isNull(tag)) {
//                result.setSuccess(false);
//                result.setMessage("tagId=" + tagId + " not found");
//                return result;
//            }
//            TagVo tagVO = TagVoBuilder.create(tag);
//            result.setData(tagVO);
//
//            // category
//            Long catId = tagVO.getTagCategoryId();
//            List<TagCategory> catList = tagCategoryService.getParentList(graph, catId);
//            String tagCategoryName = TagCatNamesBuilder.get(catList);
//            tagVO.setTagCategoryName(tagCategoryName);
//
//            // objectKeyCount
//            Map<String, Long> tagIdToCount = queryObjectKeyCount(graph, Sets.newHashSet(tagId));
//            Long tagIdCount = tagIdToCount.getOrDefault(tagId + "", 0L);
//            tagVO.setObjectKeyCount(tagIdCount);
//
//            // add tag parameters
//            if (StringUtils.isBlank(tag.getRule())) {
//                return result;
//            }
//            TagRule tagRule = TagRuleParser.parseRuleExpression(tag.getRule());
//            if (tagRule.isEmptyParamIds()) {
//                return result;
//            }
//            Map<Long, TagParameter> parameters = this.getTagParameters(graph, tagRule.getLogicParamIds());
//            TagVoBuilder.addLogicParameters(parameters, tagRule, tagVO);
//        } catch (Exception e) {
//            result.setMessage(e.getMessage());
//            LOG.error(e);
//        }
//        return result;
//    }
//
//    private TagCategoryVo findChildren(TagCategoryVo tree, List<TagCategory> list) {
//        List<TagCategoryVo> children = new ArrayList<>();
//        for (TagCategory tcc : list) {
//            if (tcc.getParent() == tree.getId()) {
//                TagCategoryVo node = TagCategoryVoBuilder.create(tcc);
//                children.add(findChildren(node, list));
//            }
//        }
//        tree.setChildren(children);
//        return tree;
//    }
//
//    @Override
//    public Result insert(TagReq params) {
//        Result result = checkTag(params, true);
//        if (!result.isSuccess()) {
//            return result;
//        }
//        try {
//            Tag tag = this.upsertTag(params, true);
//            result.setData(tag.getId());
//            LOG.info("Success to insert tag[{0}/{1}]", tag.getGraph(), tag.getId() + "");
//            this.syncUpdateEs(tag.getGraph(), tag.getId());
//        } catch (Exception e) {
//            result.setSuccess(false);
//            result.setMessage(e.getMessage());
//            LOG.error(e);
//        }
//        return result;
//    }
//
//    @Override
//    public Result update(TagReq params) {
//        Result result = checkTag(params, false);
//        if (!result.isSuccess()) {
//            return result;
//        }
//        try {
//            Tag tag = this.upsertTag(params, false);
//            LOG.info("Success to update tag[{0}/{1}]", tag.getGraph(), tag.getId() + "");
//            this.syncUpdateEs(tag.getGraph(), tag.getId());
//        } catch (Exception e) {
//            result.setSuccess(false);
//            result.setMessage(e.getMessage());
//            LOG.error(e);
//        }
//        return result;
//    }
//
//    @Override
//    public Result delete(TagReq params) {
//        Result result = new Result();
//        try {
//            String graph = params.getGraph();
//            long tagId = params.getTagId();
//            List<TagDependency> list = tagDependencyDAO.getTagDependenciesByFromTagId(graph, tagId);
//            if (!list.isEmpty()) {
//                result.setSuccess(false);
//                Set<Long> toTagIds = list.stream().map(TagDependency::getId).collect(Collectors.toSet());
//                String message = MessageFormat.format(
//                        "It cannot delete tagId[{0}], because it is dependent on tags {1}",
//                        tagId + "", toTagIds.toString());
//                result.setMessage(message);
//                return result;
//            }
//            Tag tag = tagDAO.findOne(params.getTagId());
//            tag.setEnabledFlag(Constants.N);
//            tagDAO.save(tag);
//            this.syncUpdateEs(graph, tagId);
//        } catch (Exception e) {
//            result.setSuccess(false);
//            result.setMessage(e.getMessage());
//            LOG.error(e);
//        }
//        return result;
//    }
//
//    ///////////////////////
//    // private functions
//    ///////////////////////
//    private Tag upsertTag(TagReq params, boolean isNew) {
//        String graph = params.getGraph();
//        Tag tag = TagBuilder.create(params);
//        if (Objects.isNull(params.getOriginalRule())) {
//            if (isNew) {
//                return tagDAO.save(tag);
//            }
//            Tag tagDB = tagDAO.findOne(tag.getId());
//            TagBuilder.update(tag, tagDB);
//            return tagDAO.save(tagDB);
//        }
//        tag.setOriginalRule(JSON.toJSONString(params.getOriginalRule()));
//        String rule = LogicExpress.toExpression(tag.getOriginalRule());
//        tag.setRule(rule);
//        TagRule tagRule = TagRuleParser.parseRuleExpression(tag.getRule());
//
//        // tag
//        tag = this.upsertTag(graph, tag, tagRule, isNew);
//        long tagId = tag.getId();
//
//        // tag_dependency
//        this.autoUpsertTagDependencies(graph, tagId, tagRule, isNew);
//
//        // tag_schema
//        this.autoUpsertTagSchemas(graph, tagId, tagRule, isNew);
//
//        // tag_parameter
//        //this.autoUpsertTagParameter(graph, tag, isNew);
//        return tag;
//    }
//
//    private Tag upsertTag(String graph, Tag tag, TagRule tagRule, boolean isNew) {
//        // tag.analytics_mode
//        tag.setAnalyticsMode(tagRule.getAnalyticsMode());
//
//        // tag.value_schema
//        if (tag.getAnalyticsMode() == AnalyticsMode.STAT) {
//            SqlUtils.getMultiValueSchema(tag.getRule());
//        }
//
//        // tag.rule_script
//        Map<Long, TagParameter> parameterMap = this.getTagParameters(graph,
//                tagRule.getLogicParamIds());
//        RuleScript ruleScript = TagRuleParser.getRuleScript(parameterMap, tagRule);
//        tag.setRuleScript(JSON.toJSONString(ruleScript));
//
//        // tag
//        if (isNew) {
//            return tagDAO.save(tag);
//        }
//        Tag tagDB = tagDAO.findOne(tag.getId());
//        TagBuilder.update(tag, tagDB);
//        return tagDAO.save(tagDB);
//    }
//
//    private void autoUpsertTagDependencies(String graph, long tagId, TagRule tagRule, boolean isNew) {
//        Set<Long> fromTagIds = tagRule.getTagParamIds();
//        if (isNew) {
//            List<TagDependency> tagDependencies = TagDependencyBuilder.create(fromTagIds, graph, tagId);
//            tagDependencyDAO.save(tagDependencies);
//            return;
//        }
//
//        // fromTagIds(1/2/3) + fromTagIdToTD(1/2/4) -> insert 3 delete 4
//        Map<Long, TagDependency> fromTagIdToTD = this.getTagDependencies(graph, tagId);
//        if (fromTagIdToTD.isEmpty()) {
//            List<TagDependency> tagDependencies = TagDependencyBuilder.create(fromTagIds, graph, tagId);
//            tagDependencyDAO.save(tagDependencies);
//            return;
//        }
//
//        // insert
//        List<TagDependency> insertTDList = new ArrayList<>();
//        for (Long fromTagId : fromTagIds) {
//            if (!fromTagIdToTD.containsKey(fromTagId)) {
//                insertTDList.add(TagDependencyBuilder.create(fromTagId, graph, tagId));
//            }
//        }
//        tagDependencyDAO.save(insertTDList);
//
//        // delete
//        List<TagDependency> deleteTDList = new ArrayList<>();
//        for (TagDependency td : fromTagIdToTD.values()) {
//            if (!fromTagIds.contains(td.getFromTagId())) {
//                deleteTDList.add(td);
//            }
//        }
//        tagDependencyDAO.delete(deleteTDList);
//    }
//
//    private void autoUpsertTagSchemas(String graph, long tagId, TagRule tagRule, boolean isNew) {
//        ScriptContext ctx = tagRule.getScriptContext();
//        List<TagSchema> tagSchemas = TagSchemaBuilder.create(ctx, graph, tagId);
//        if (isNew) {
//            tagSchemaDAO.save(tagSchemas);
//            return;
//        }
//        // tagSchemas(1/2/3) + schemaToTS(1/2/4) -> update 1,2 insert 3 delete 4
//        Map<String, TagSchema> schemaToTS = this.getTagSchemas(graph, tagId);
//        if (schemaToTS.isEmpty()) {
//            tagSchemaDAO.save(tagSchemas);
//            return;
//        }
//        // insert + update
//        List<TagSchema> updateTSList = new ArrayList<>();
//        for (TagSchema ts : tagSchemas) {
//            String schema = ts.getSchema();
//            if (schemaToTS.containsKey(schema)) {
//                TagSchema sch = schemaToTS.get(schema);
//                ts.setId(sch.getId());
//                updateTSList.add(ts);
//            } else {
//                updateTSList.add(ts);
//            }
//        }
//        tagSchemaDAO.save(updateTSList);
//
//        // delete
//        List<TagSchema> deleteTSList = new ArrayList<>();
//        Map<String, TagSchema> tagSchemaMap = tagSchemas.stream()
//                .collect(Collectors.toMap(TagSchema::getSchema, Function.identity()));
//        for (TagSchema sch : schemaToTS.values()) {
//            if (!tagSchemaMap.containsKey(sch.getSchema())) {
//                deleteTSList.add(sch);
//            }
//        }
//        tagSchemaDAO.delete(deleteTSList);
//    }
//
//    private void autoUpsertTagParameter(String graph, Tag tag, boolean isNew) {
//        if (isNew) {
//            TagParameter tp = TagParameterBuilder.create(tag);
//            tagParameterDAO.save(tp);
//            return;
//        }
//        String reference = TagUtils.getTagReference(tag.getId().toString());
//        TagParameter tp = tagParameterDAO.getTagParameterByReference(graph, reference);
//        TagParameterBuilder.update(tag, tp);
//        tagParameterDAO.save(tp);
//    }
//
//    private List<TagVo> mergeTagParameters(List<Tag> tagList, String graph) {
//        List<TagVo> tagVOList = new ArrayList<>();
//        Map<Long, TagRule> tagIdToTagRules = new HashMap<>();
//        Set<Long> allParamIds = new HashSet<>();
//        // tag
//        for (Tag tag : tagList) {
//            long tagId = tag.getId();
//            TagVo tagVO = TagVoBuilder.create(tag);
//            tagVOList.add(tagVO);
//
//            if (StringUtils.isBlank(tagVO.getRule())) {
//                continue;
//            }
//            TagRule tagRule = TagRuleParser.parseRuleExpression(tagVO.getRule());
//            tagIdToTagRules.put(tagId, tagRule);
//            allParamIds.addAll(tagRule.getLogicParamIds());
//        }
//        if (allParamIds.isEmpty()) {
//            return tagVOList;
//        }
//        // tag_parameter
//        Map<Long, TagParameter> parameters = this.getTagParameters(graph, allParamIds);
//        for (TagVo tagVO : tagVOList) {
//            long tagId = tagVO.getTagId();
//            TagRule tagRule = tagIdToTagRules.get(tagId);
//            if (Objects.isNull(tagRule)) {
//                continue;
//            }
//            TagVoBuilder.addLogicParameters(parameters, tagRule, tagVO);
//        }
//        return tagVOList;
//    }
//
//    private Page<Tag> findPageTags(TagPageQo params) {
//        // specification
//        String graph = params.getGraph();
//        String userId = params.getUserId();
//        TagGroup tagGroup = params.getTagGroup();
//        Set<TagStatus> tagStatuses = params.getTagStatuses();
//        tagStatuses = tagStatuses == null ? Collections.emptySet() : tagStatuses;
//        String tagCatIdsStr = params.getTagCategoryIds();
//        tagCatIdsStr = tagCatIdsStr == null ? "" : tagCatIdsStr;
//        tagCatIdsStr = "[" + tagCatIdsStr + "]";
//        Set<Long> tagCatIds = JSON.parseObject(tagCatIdsStr, new TypeReference<Set<Long>>() {
//        });
//        tagCatIds = tagCategoryService.getChildrenCatIds(graph, tagCatIds);
//        String upTimeStr = params.getUpTime();
//        String startUpTime = StringUtils.substringBefore(upTimeStr, "|");
//        String endUpTime = StringUtils.substringAfter(upTimeStr, "|");
//        String tagName = params.getKeyword();
//        Specification<Tag> specification = Specifications.<Tag>and()
//                .eq("graph", graph)
//                .eq("enabledFlag", Constants.Y)
//                .eq(StringUtils.isNotBlank(userId), "applyBy", userId)
//                .eq(Objects.nonNull(tagGroup), "tagGroup", tagGroup)
//                .in(!CollectionUtils.isEmpty(tagStatuses), "tagStatus", tagStatuses.toArray())
//                .in(!tagCatIds.isEmpty(), "tagCategoryId", tagCatIds.toArray())
//                .ge(DateUtils.isDate(startUpTime), "upTime", DateUtils.toLocal(startUpTime))
//                .le(DateUtils.isDate(endUpTime), "upTime", DateUtils.toLocal(endUpTime))
//                .like(StringUtils.isNotBlank(tagName), "tagName", "%" + tagName + "%")
//                .build();
//        params.addSortOrder("updatedDt", PageQo.Direction.DESC);
//        PageRequest pageRequest = PageRequestBuilder.create(params);
//        return tagDAO.findAll(specification, pageRequest);
//    }
//
//    private Map<Long, TagParameter> getTagParameters(String graph, Set<Long> ids) {
//        if (ids.isEmpty()) {
//            return Collections.emptyMap();
//        }
//        return tagParameterDAO.getTagParameters(graph, ids)
//                .stream().collect(Collectors.toMap(TagParameter::getId, Function.identity()));
//    }
//
//    private Map<Long, TagDependency> getTagDependencies(String graph, long toTagId) {
//        return tagDependencyDAO.getTagDependencies(graph, toTagId)
//                .stream().collect(Collectors.toMap(TagDependency::getFromTagId, Function.identity()));
//    }
//
//    private Map<String, TagSchema> getTagSchemas(String graph, long tagId) {
//        return tagSchemaDAO.getTagSchemas(graph, tagId)
//                .stream().collect(Collectors.toMap(TagSchema::getSchema, Function.identity()));
//    }
//
//    private Result checkTag(TagReq params, boolean isNew) {
//        Result result = new Result();
//        long tagId = params.getTagId();
//        if (!isNew && tagId <= 0) {
//            result.setSuccess(false);
//            result.setMessage("tagId not found " + tagId);
//        }
//        if (StringUtils.isBlank(params.getTagName())) {
//            result.setSuccess(false);
//            result.setMessage("tagName cannot be empty");
//        }
//        return result;
//    }
//
//    private void syncUpdateEs(String graph, Long tagId) {
//        boolean success = tagPersistService.insertOrUpdate(graph, tagId);
//        LOG.info("Sync update tag to es[{0}/{1}], success={2}", graph, tagId + "", success);
//    }
//}
