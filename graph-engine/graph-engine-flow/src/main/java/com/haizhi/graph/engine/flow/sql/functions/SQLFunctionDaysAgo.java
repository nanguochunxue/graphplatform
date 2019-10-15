package com.haizhi.graph.engine.flow.sql.functions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chengmo on 2018/4/3.
 */
public class SQLFunctionDaysAgo extends SQLFunction {

    public static final String NAME = "#daysAgo";
    public static final String PATTERN = NAME + "\\(.*\\)";

    @Override
    public String rebuild(String sql) {

        return null;
    }

    @Override
    public String getParam() {
        return null;
    }

    public static void main(String[] args) {
        String sql = "select t.date>#daysAgo(365) from xxx";
        Matcher matcher = Pattern.compile(PATTERN).matcher(sql);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }
}
