package com.haizhi.graph.engine.base.rule.script;

/**
 * Created by chengmo on 2018/2/8.
 */
public abstract class RuleScript {

    public abstract void setScript(String script);

    public abstract String getSource();

    public abstract void setSource(String source);

    public abstract ScriptContext getContext();

    public abstract void setContext(ScriptContext context);

    public abstract ScriptParams getParams();

    public abstract void setParams(ScriptParams params);

    public abstract Object execute();
}
