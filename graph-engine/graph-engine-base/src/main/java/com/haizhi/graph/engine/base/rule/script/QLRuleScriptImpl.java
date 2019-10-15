package com.haizhi.graph.engine.base.rule.script;

import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.engine.base.rule.Rule;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

/**
 * Created by chengmo on 2018/2/8.
 */
public class QLRuleScriptImpl extends RuleScript {

    private static final GLog LOG = LogFactory.getLogger(QLRuleScriptImpl.class);
    private static final String SOURCE_FIELD = "source";
    private static final String CONTEXT_FIELD = "context";
    private static ExpressRunner runner = new ExpressRunner();

    private String source;
    private ScriptContext context;
    private ScriptParams params = new ScriptParams();

    @Override
    public void setScript(String script) {
        JSONObject root = JSONObject.parseObject(script);
        this.source = root.getString(SOURCE_FIELD);
        this.context = root.getObject(CONTEXT_FIELD, ScriptContext.class);
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public ScriptContext getContext() {
        return context;
    }

    @Override
    public void setContext(ScriptContext context) {
        this.context = context;
    }

    @Override
    public ScriptParams getParams() {
        return params;
    }

    @Override
    public void setParams(ScriptParams params) {
        if (params != null){
            this.params = params;
        }
    }

    @Override
    public Object execute() {
        Object result = Rule.INVALID_RESULT;
        try {
            DefaultContext<String, Object> context = new DefaultContext<>();
            context.putAll(params);
            result = runner.execute(source, context, null, false, true);
        } catch (Exception e) {
            LOG.error(e);
        }
        return result;
    }
}
