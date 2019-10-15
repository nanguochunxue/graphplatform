package com.haizhi.graph.server.api.core;

import com.haizhi.graph.server.api.bean.StoreURL;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by chengmo on 2019/6/19.
 */
public class EsBuilder {

    public static final String V6_X = "6.";
    public static final String V5_X = "5.";

    public static String getIndex(StoreURL storeURL, String index, String type) {
        String version = storeURL.getStoreVersion();
        String finalIndex = index.toLowerCase();
        if (StringUtils.isBlank(version)) {
            return finalIndex;
        }
        if (version.startsWith(V6_X)) {
            return index + "." + type.toLowerCase();
        } else if (version.startsWith(V5_X)) {
            return finalIndex;
        }
        return finalIndex;
    }
    public static String getEs6Index(String index, String type) {
        return (index + "." + type).toLowerCase();
    }
}
