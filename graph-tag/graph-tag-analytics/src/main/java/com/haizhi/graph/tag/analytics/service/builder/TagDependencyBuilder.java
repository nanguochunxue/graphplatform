package com.haizhi.graph.tag.analytics.service.builder;

import com.haizhi.graph.tag.core.domain.TagDependency;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by chengmo on 2018/5/4.
 */
public class TagDependencyBuilder {

    public static List<TagDependency> create(Set<Long> fromTagIds, String graph, long tagId) {
        List<TagDependency> list = new ArrayList<>();
        for (Long fromTagId : fromTagIds) {
            TagDependency td = new TagDependency();
            td.setGraph(graph);
            td.setFromTagId(fromTagId);
            td.setToTagId(tagId);
        }
        return list;
    }

    public static TagDependency create(long fromTagId, String graph, long tagId) {
        TagDependency td = new TagDependency();
        td.setGraph(graph);
        td.setFromTagId(fromTagId);
        td.setToTagId(tagId);
        return td;
    }
}
