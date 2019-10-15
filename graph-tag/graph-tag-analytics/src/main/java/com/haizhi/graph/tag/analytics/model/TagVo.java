package com.haizhi.graph.tag.analytics.model;

import com.haizhi.graph.engine.base.rule.logic.LogicRule;
import com.haizhi.graph.tag.core.domain.DataType;
import com.haizhi.graph.tag.core.domain.TagGroup;
import com.haizhi.graph.tag.core.domain.TagParameterType;
import com.haizhi.graph.tag.core.domain.TagStatus;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/5/3.
 */
@Data
public class TagVo {

    private long tagId;
    private String graph;

    // tag_category
    private long tagCategoryId;
    private String tagCategoryName;

    // tag
    private String tagName;
    private String tagDesc;
    private TagGroup tagGroup;
    private TagStatus tagStatus;
    private Date upTime;
    private Date applyUpTime;
    private String applyBy;
    private String approveBy;
    private int weight;
    private DataType dataType;
    private LogicRule originalRule;
    private String rule;
    private Date createdDt;
    private Date updatedDt;
    private Map<String, Parameter> ruleParams = new HashMap<>();

    // tag.value
    private Object tagValue;

    // aggregation
    private long objectKeyCount;

    public void addParameter(String expression, Parameter param){
        this.ruleParams.put(expression, param);
    }

    @Data
    public static class Parameter{
        private long id;
        private TagParameterType type;
        private String name;
        private String reference;
        private String value;
    }
}
