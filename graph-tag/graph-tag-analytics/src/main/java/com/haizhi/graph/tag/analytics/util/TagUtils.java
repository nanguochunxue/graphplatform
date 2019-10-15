package com.haizhi.graph.tag.analytics.util;

import com.haizhi.graph.tag.analytics.bean.TagValue;

import java.text.MessageFormat;

/**
 * Created by chengmo on 2018/4/24.
 */
public class TagUtils {

    public static String getTagEsIndex(String graph){
        return graph + Constants.TAG_SUFFIX;
    }

    public static String getTagReference(String tagId){
        return MessageFormat.format("{0}.tag_id_{1}", TagValue._schema, tagId);
    }

    public static String appendTagIdPrefix(String str){
        return MessageFormat.format("tag_id_{0}", str);
    }
}
