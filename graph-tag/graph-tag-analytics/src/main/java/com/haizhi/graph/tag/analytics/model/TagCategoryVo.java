package com.haizhi.graph.tag.analytics.model;

import lombok.Data;

import java.util.List;

/**
 * Created by chengmo on 2018/7/20.
 */
@Data
public class TagCategoryVo {
    private long id;
    private String graph;
    private String tagCategoryName;
    private String tagCategoryDesc;
    private long parent;
    private int level;
    private boolean isLeaf;
    private List<TagCategoryVo> children;
}
