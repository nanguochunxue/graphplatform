package com.haizhi.graph.tag.analytics.service.builder;

import com.haizhi.graph.tag.analytics.util.TagUtils;
import com.haizhi.graph.tag.core.domain.Tag;
import com.haizhi.graph.tag.core.domain.TagParameter;
import com.haizhi.graph.tag.core.domain.TagParameterType;

/**
 * Created by chengmo on 2018/5/7.
 */
public class TagParameterBuilder {

    public static TagParameter create(Tag tag){
        TagParameter tp = new TagParameter();
        tp.setGraph(tag.getGraph());
        tp.setType(TagParameterType.TAG);
        tp.setName(tag.getTagName());
        tp.setReference(TagUtils.getTagReference(tag.getId().toString()));
        tp.setValue(tag.getValueSchema());
        return tp;
    }

    public static TagParameter update(Tag tag, TagParameter tp){
        if (tp == null){
            tp = new TagParameter();
        }
        tp.setGraph(tag.getGraph());
        tp.setType(TagParameterType.TAG);
        tp.setName(tag.getTagName());
        tp.setReference(TagUtils.getTagReference(tag.getId().toString()));
        tp.setValue(tag.getValueSchema());
        return tp;
    }
}
