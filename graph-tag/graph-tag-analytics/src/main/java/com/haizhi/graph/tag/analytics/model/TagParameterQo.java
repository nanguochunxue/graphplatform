package com.haizhi.graph.tag.analytics.model;

import com.haizhi.graph.common.model.v0.PageQo;
import com.haizhi.graph.tag.core.domain.TagParameterType;
import lombok.Data;

/**
 * Created by chengmo on 2018/7/23.
 */
@Data
public class TagParameterQo extends PageQo {
    private String graph;
    private TagParameterType type;
    private String name;
    private String reference;
}
