package com.haizhi.graph.engine.base.rule;

import com.haizhi.graph.engine.base.rule.builder.RuleBuilder;
import com.haizhi.graph.engine.base.rule.script.QLRuleScriptImpl;
import com.haizhi.graph.engine.base.rule.script.RuleScript;

/**
 * Created by chengmo on 2018/2/8.
 */
public class BasicRule extends Rule {

    protected String name;
    protected final RuleScript script;
    protected RuleBuilder builder;

    public BasicRule() {
        this.script = new QLRuleScriptImpl();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RuleScript script() {
        return script;
    }

    @Override
    public RuleBuilder builder() {
        return builder;
    }

    @Override
    public Object execute() {
        return script.execute();
    }
}
