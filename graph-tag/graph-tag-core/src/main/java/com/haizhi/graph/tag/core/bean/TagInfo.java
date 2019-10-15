package com.haizhi.graph.tag.core.bean;

import com.haizhi.graph.tag.core.domain.AnalyticsMode;
import com.haizhi.graph.tag.core.domain.DataType;
import com.haizhi.graph.tag.core.domain.TagGroup;
import lombok.Data;

/**
 * Created by chengmo on 2018/3/9.
 */
@Data
public class TagInfo {
    private long tagId;
    // tag_category
    private long tagCategoryId;
    // tag
    private String graph;
    private String tagName;
    private String tagDesc;
    private TagGroup tagGroup;
    private String sourceType;
    private AnalyticsMode analyticsMode;
    private DataType dataType;
    private String defaultValue;
    private String valueSchema;
    private String valueOptions;
    private boolean valueOptionsEnabled;
    private boolean valueHistoryEnabled;
    private String rule;
    private String ruleScript;
}
