package com.haizhi.graph.engine.flow.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chengmo on 2018/4/4.
 */
public class StrPatternUtils {

    /* Example: $.count(reg_mount1) */
    private static Pattern NESTED_PATH = Pattern.compile("\\$\\.\\w+\\(\\w+\\)");

    public static Matcher matcher(String str){
        return NESTED_PATH.matcher(str);
    }

}
