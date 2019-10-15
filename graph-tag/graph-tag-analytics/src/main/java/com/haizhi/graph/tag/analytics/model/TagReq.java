package com.haizhi.graph.tag.analytics.model;

import com.haizhi.graph.engine.base.rule.logic.LogicRule;
import com.haizhi.graph.tag.core.domain.TagGroup;
import com.haizhi.graph.tag.core.domain.TagParameterType;
import com.haizhi.graph.tag.core.domain.TagStatus;
import lombok.Data;

import java.util.Map;

/**
 * Created by chengmo on 2018/4/27.
 */
@Data
public class TagReq {

    private long tagId;
    private String graph;

    // user
    private String userId;

    // tag_category
    private long tagCategoryId;
    private long parentId;
    private String tagCategoryName;
    private String tagCategoryDesc;

    // tag
    private String tagName;
    private String tagDesc;
    private TagGroup tagGroup = TagGroup.SYSTEM;
    private TagStatus tagStatus = TagStatus.CREATED;
    private String upTime;
    private LogicRule originalRule;
    private String rule;
    private Map<String, Parameter> ruleParams;
    private String status;

    @Data
    public static class Parameter {
        private long id;
        private TagParameterType type;
        private String name;
        private String reference;
        private String value;
    }
}
