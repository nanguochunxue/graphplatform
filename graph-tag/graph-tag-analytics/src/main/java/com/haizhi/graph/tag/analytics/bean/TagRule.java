package com.haizhi.graph.tag.analytics.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.haizhi.graph.engine.base.rule.script.RuleScript;
import com.haizhi.graph.engine.base.rule.script.ScriptContext;
import com.haizhi.graph.tag.core.domain.AnalyticsMode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chengmo on 2018/5/4.
 */
public class TagRule {

    private String ruleExpression;
    private Map<String, Long> logicParams = new HashMap<>();
    private Set<Long> logicParamIds = new HashSet<>();
    private AnalyticsMode analyticsMode;
    private RuleScript ruleScript;

    public TagRule(String ruleExpression) {
        this.ruleExpression = ruleExpression;
    }

    @JSONField(serialize = false)
    public ScriptContext getScriptContext(){
        if (ruleScript == null){
            return new ScriptContext();
        }
        return ruleScript.getContext();
    }

    public Set<Long> getTagParamIds(){
        Set<Long> set = new HashSet<>();
        for (Map.Entry<String, Long> entry : logicParams.entrySet()) {
            if (entry.getKey().startsWith("@tag")){
                set.add(entry.getValue());
            }
        }
        return set;
    }

    public boolean isEmptyParams(){
        return logicParams.isEmpty();
    }

    public boolean isEmptyParamIds(){
        return logicParamIds.isEmpty();
    }

    public void addLogicParam(String expression, long paramId){
        this.logicParams.put(expression, paramId);
    }

    public void addLogicParamId(long paramId){
        this.logicParamIds.add(paramId);
    }

    public String getRuleExpression() {
        return ruleExpression;
    }

    public void setRuleExpression(String ruleExpression) {
        this.ruleExpression = ruleExpression;
    }

    public Map<String, Long> getLogicParams() {
        return logicParams;
    }

    public Set<Long> getLogicParamIds() {
        return logicParamIds;
    }

    public AnalyticsMode getAnalyticsMode() {
        return analyticsMode;
    }

    public void setAnalyticsMode(AnalyticsMode analyticsMode) {
        this.analyticsMode = analyticsMode;
    }

    public RuleScript getRuleScript() {
        return ruleScript;
    }

    public void setRuleScript(RuleScript ruleScript) {
        this.ruleScript = ruleScript;
    }
}
