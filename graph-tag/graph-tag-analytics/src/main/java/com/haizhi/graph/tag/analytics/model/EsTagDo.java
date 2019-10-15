package com.haizhi.graph.tag.analytics.model;

import com.haizhi.graph.tag.core.domain.AnalyticsMode;
import com.haizhi.graph.tag.core.domain.DataType;
import com.haizhi.graph.tag.core.domain.TagGroup;
import com.haizhi.graph.tag.core.domain.TagStatus;
import lombok.Data;

import java.util.Date;

/**
 * Created by chengmo on 2018/8/8.
 */
@Data
public class EsTagDo {
    private String graph;
    private Long tagCategoryId;
    private String tagCategoryName;
    private String tagName;
    private String tagDesc;
    private TagGroup tagGroup;
    private TagStatus tagStatus;
    private TagStatus tagStatusBak;
    private Date upTime;
    private Date applyUpTime;
    private String applyBy;
    private String approveBy;
    private Integer weight;
    private String sourceType;
    private AnalyticsMode analyticsMode;
    private DataType dataType;
    private String defaultValue;
    private String valueSchema;
    private String valueOptions;
    private Boolean valueOptionsEnabled;
    private Boolean valueHistoryEnabled;
    private String originalRule;
    private String rule;
    private String ruleScript;
    private String enabledFlag;
    private Date createdDt;
    private Date updatedDt;
    private Long createdById;
    private Long updateById;
}
