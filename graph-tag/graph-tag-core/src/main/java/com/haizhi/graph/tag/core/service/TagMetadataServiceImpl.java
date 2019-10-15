package com.haizhi.graph.tag.core.service;

import com.google.common.collect.Sets;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.tag.core.bean.TagDomain;
import com.haizhi.graph.tag.core.bean.TagInfo;
import com.haizhi.graph.tag.core.bean.TagSchemaInfo;
import com.haizhi.graph.tag.core.dao.TagDAO;
import com.haizhi.graph.tag.core.dao.TagDependencyDAO;
import com.haizhi.graph.tag.core.dao.TagParameterDAO;
import com.haizhi.graph.tag.core.dao.TagSchemaDAO;
import com.haizhi.graph.tag.core.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by chengmo on 2018/3/9.
 */
@Service
public class TagMetadataServiceImpl implements TagMetadataService {

    private static final GLog LOG = LogFactory.getLogger(TagMetadataServiceImpl.class);

    @Autowired
    private TagDAO tagDAO;
    @Autowired
    private TagSchemaDAO tagSchemaDAO;
    @Autowired
    private TagDependencyDAO tagDependencyDAO;
    @Autowired
    private TagParameterDAO tagParameterDAO;

    @Override
    public TagDomain getTagDomain(String graphName) {
        TagDomain tagDomain = new TagDomain(graphName);
        try {
            List<Tag> tagList = tagDAO.findAll(graphName);
            List<TagSchema> tagSchemas = tagSchemaDAO.getTagSchemas(graphName);
            List<TagDependency> dependencies = tagDependencyDAO.getTagDependencies(graphName);
            tagDomain.setDependencies(dependencies);

            // TagParameter.type=SQL
            Set<String> types = Sets.newHashSet(TagParameterType.SQL.name());
            Map<Long, TagParameter> parameters = tagParameterDAO.getTagParametersByTypes(graphName, types).stream()
                    .collect(Collectors.toMap(TagParameter::getId, Function.identity()));
            tagDomain.setParameters(parameters);

            // TagInfo
            Map<Long, TagInfo> tagInfoMap = new HashMap<>();
            for (Tag tag : tagList) {
                long tagId = tag.getId();
                TagInfo tagInfo = new TagInfo();
                tagInfo.setTagId(tagId);
                tagInfo.setTagName(tag.getTagName());
                tagInfo.setTagDesc(tag.getTagDesc());
                tagInfo.setTagGroup(tag.getTagGroup());
                tagInfo.setSourceType(tag.getSourceType());
                tagInfo.setAnalyticsMode(tag.getAnalyticsMode());
                tagInfo.setDataType(tag.getDataType());
                tagInfo.setDefaultValue(tag.getDefaultValue());
                tagInfo.setValueSchema(tag.getValueSchema());
                tagInfo.setValueOptions(tag.getValueOptions());
                if (Objects.isNull(tag.getValueOptionsEnabled())){
                    tagInfo.setValueOptionsEnabled(true);
                } else {
                    tagInfo.setValueOptionsEnabled(tag.getValueOptionsEnabled());
                }
                if (Objects.isNull(tag.getValueHistoryEnabled())){
                    tagInfo.setValueHistoryEnabled(false);
                } else {
                    tagInfo.setValueHistoryEnabled(tag.getValueOptionsEnabled());
                }
                tagInfo.setRule(tag.getRule());
                tagInfo.setRuleScript(tag.getRuleScript());
                tagInfoMap.put(tagId, tagInfo);
            }
            tagDomain.setTagInfoMap(tagInfoMap);

            // TagSchemaInfo
            for (TagSchema ts : tagSchemas) {
                TagSchemaInfo info = new TagSchemaInfo();
                info.setTagId(ts.getTagId());
                info.setGraph(ts.getGraph());
                info.setType(ts.getType());
                info.setSchema(ts.getSchema());
                info.setFields(ts.getFields());
                tagDomain.getTagSchemaInfoList().add(info);
            }

        } catch (Exception e) {
            LOG.info(e);
        }
        return tagDomain;
    }
}
