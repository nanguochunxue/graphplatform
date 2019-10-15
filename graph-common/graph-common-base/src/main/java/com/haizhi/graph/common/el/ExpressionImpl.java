package com.haizhi.graph.common.el;

import com.haizhi.graph.common.util.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2017/12/6.
 */
public class ExpressionImpl implements Expression {

    protected String expression;
    protected List<String> elements;
    protected boolean validExpression;

    public ExpressionImpl(String expr, List<String> elements, boolean valid) {
        this.expression = expr;
        this.elements = elements;
        this.validExpression = StringUtils.isNotBlank(expression) && elements != null && !elements.isEmpty();
    }

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public boolean validExpression() {
        return validExpression;
    }

    @Override
    public String format(Map<String, Object> vars) {
        String result = expression;
        for (String element : elements) {
            String field = element.replaceAll("[$|{|}]", "");
            String value = Getter.get(field, vars);
            result = result.replace(element, value);
        }
        return result;
    }

    public String toString() {
        String expr = this.getExpression();
        return expr == null ? "" : expr;
    }
}
