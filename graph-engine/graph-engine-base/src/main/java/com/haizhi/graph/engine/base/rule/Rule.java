package com.haizhi.graph.engine.base.rule;

import com.haizhi.graph.engine.base.rule.builder.RuleBuilder;
import com.haizhi.graph.engine.base.rule.script.RuleScript;

/**
 * Created by chengmo on 2018/2/8.
 */
public abstract class Rule {

    public static final String INVALID_RESULT = "$";

    public static final String BOOLEAN_TRUE = "1";

    public abstract String getName();

    public abstract RuleScript script();

    public abstract RuleBuilder builder();

    public abstract Object execute();

    public static String format(String expr){
        return expr.replaceAll("\\(|\\)", "\\$");
    }
}
