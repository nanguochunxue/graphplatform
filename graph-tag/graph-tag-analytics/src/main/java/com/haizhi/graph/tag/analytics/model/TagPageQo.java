package com.haizhi.graph.tag.analytics.model;

import com.haizhi.graph.common.model.v0.PageQo;
import com.haizhi.graph.tag.core.domain.TagGroup;
import com.haizhi.graph.tag.core.domain.TagStatus;
import lombok.Data;

import java.util.Set;

/**
 * Created by chengmo on 2018/6/19.
 */
@Data
public class TagPageQo extends PageQo {
    private String graph;
    private String userId;
    private TagGroup tagGroup;
    private Set<TagStatus> tagStatuses;
    private String tagCategoryIds;
    private String upTime;
    private String keyword;
}
