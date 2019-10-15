package com.haizhi.graph.tag.analytics.model;

import lombok.Data;

import java.util.Set;

/**
 * Created by chengmo on 2018/7/26.
 */
@Data
public class TagValueReq {
    private String graph;
    private Long tagId;
    private Set<String> objectKeys;
}
