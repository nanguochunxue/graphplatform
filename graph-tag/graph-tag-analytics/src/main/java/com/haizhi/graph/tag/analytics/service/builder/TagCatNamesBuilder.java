package com.haizhi.graph.tag.analytics.service.builder;

import com.haizhi.graph.tag.core.domain.TagCategory;

import java.util.List;

/**
 * Created by chengmo on 2018/8/10.
 */
public class TagCatNamesBuilder {

    public static String get(List<TagCategory> list){
        StringBuilder sb = new StringBuilder();
        for (TagCategory tc : list) {
            sb.append(tc.getTagCategoryName()).append(">");
        }
        if (sb.length() > 0){
            sb = sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
