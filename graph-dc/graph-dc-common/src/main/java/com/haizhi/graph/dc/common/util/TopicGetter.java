package com.haizhi.graph.dc.common.util;

import java.text.MessageFormat;

/**
 * Created by chengmo on 2018/10/24.
 */
public class TopicGetter {
    public static String get(String topicPrefix, String graph, String schema) {
        return MessageFormat.format("{0}.{1}.{2}", topicPrefix, graph, schema);
    }
}
