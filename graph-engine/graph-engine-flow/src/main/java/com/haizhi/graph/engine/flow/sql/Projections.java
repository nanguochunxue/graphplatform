package com.haizhi.graph.engine.flow.sql;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by chengmo on 2018/4/3.
 */
public class Projections {

    public static final String id = "id";
    public static final String objectKey = "objectKey";
    public static final String name = "name";
    public static final String statTime = "statTime";
    public static final String value = "value";
    public static final String dataType = "dataType";

    private static final Set<String> KEYS = new HashSet<>(Arrays.asList(objectKey, name, statTime));

    public static boolean contains(String str){
        return KEYS.contains(str);
    }
}
