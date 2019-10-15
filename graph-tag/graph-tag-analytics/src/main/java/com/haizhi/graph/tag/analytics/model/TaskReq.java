package com.haizhi.graph.tag.analytics.model;

import lombok.Data;

import java.util.*;

/**
 * Created by chengmo on 2018/4/25.
 */
@Data
public class TaskReq {
    private String graph;
    private String type;
    private Set<Long> tagIds;
    private Map<String, List<String>> filter;
    private boolean useCache;
    private boolean debugLauncher;
}
