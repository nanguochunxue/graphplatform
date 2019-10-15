package com.haizhi.graph.common.el;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chengmo on 2017/12/6.
 */
public class ExpressionFactory {

    public static Expression createExpression(String expression){
        List<String> elements = new ArrayList<>();
        boolean validExpression = false;
        if (StringUtils.isNotBlank(expression)){
            Matcher matcher = Pattern.compile("\\$\\{\\w+\\}").matcher(expression);
            while (matcher.find()) {
                elements.add(matcher.group());
            }
            validExpression = !elements.isEmpty();
        }
        return new ExpressionImpl(expression, elements, validExpression);
    }
}
