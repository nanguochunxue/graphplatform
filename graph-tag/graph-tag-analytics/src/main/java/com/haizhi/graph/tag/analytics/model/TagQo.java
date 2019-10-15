package com.haizhi.graph.tag.analytics.model;

import lombok.Data;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by chengmo on 2018/7/27.
 */
@Data
public class TagQo {
    private String graph;
    private Set<String> objectKeys = new LinkedHashSet<>();
    private Set<Long> tagIds = new LinkedHashSet<>();

    public void addObjectKeys(String... objectKeys){
        this.objectKeys.addAll(Arrays.asList(objectKeys));
    }

    public void addTagIds(Long... tagIds){
        this.tagIds.addAll(Arrays.asList(tagIds));
    }
}
