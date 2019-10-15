package com.haizhi.graph.engine.base.rule.logic;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.constant.LogicOperator;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * Created by chengmo on 2018/7/23.
 */
public class LogicExpress {

    /**
     * Convert <LogicRule json string> to <expression>.
     *
     * @param jsonLogicRule
     * @return
     */
    public static String toExpression(String jsonLogicRule) {
        LogicRule rule = JSON.parseObject(jsonLogicRule, LogicRule.class);
        if (Objects.isNull(rule)){
            return "";
        }
        return doToExpression(new StringBuilder(), rule);
    }

    /**
     * Convert <expression> to <LogicRule bean>.
     *
     * @param expression
     * @return
     */
    public static LogicRule toLogicRule(String expression) {
        LogicRule root = new LogicRule(expression);
        doToLogicRule(root, true);
        return root;
    }

    /**
     * Format expression.
     *
     * @param expression
     * @return
     */
    public static String prettyFormat(String expression){
        LogicRule root = toLogicRule(expression);
        return doPrettyFormat(root, 0);
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private static String doToExpression(StringBuilder sb, LogicRule rule) {
        if (!rule.hasLogicOperators()) {
            return rule.getExpression();
        }
        List<LogicOperator> logicOperators = rule.getLogicOperators();
        List<LogicRule> rules = rule.getRules();
        int size = rules.size();
        for (int i = 0; i < size; i++) {
            LogicRule r = rules.get(i);
            String express = doToExpression(new StringBuilder(), r);
            if (StringUtils.isNotBlank(express)) {
                sb.append("(").append(express).append(")");
            }
            if (i < size - 1) {
                sb.append(" ").append(logicOperators.get(i)).append(" ");
            }
        }
        return sb.toString();
    }

    private static void doToLogicRule(LogicRule rule, boolean isRoot){
        String str = rule.getExpression();
        if (!StringUtils.containsAny(str.toUpperCase(), " AND ", " OR ")) {
            if (str.startsWith("(") && str.endsWith(")")){
                str = str.substring(1, str.length() - 1);
            }
            rule.setExpression(str);
            return;
        }
        if (!isRoot){
            str = str.substring(1, str.length() - 1);
        }
        int length = str.length();
        int count = 0;
        String temp = "";
        int lastSubIndex = 0;
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            temp += Character.toUpperCase(ch);
            if (temp.length() == 6) {
                temp = temp.substring(1);
            }
            if (ch == '(') {
                count++;
            } else if (ch == ')') {
                count--;
            }
            if (count != 0) {
                continue;
            }
            if (temp.contains(" AND ")) {
                rule.addRule(new LogicRule(str.substring(lastSubIndex, i - 5 + 1)));
                rule.addLogicOperator(LogicOperator.AND);
                lastSubIndex = i + 1;
            } else if (temp.contains(" OR ")) {
                rule.addRule(new LogicRule(str.substring(lastSubIndex, i - 4 + 1)));
                rule.addLogicOperator(LogicOperator.OR);
                lastSubIndex = i + 1;
            } else {
                if (lastSubIndex > 0 && i == length - 1){
                    rule.addRule(new LogicRule(str.substring(lastSubIndex, length)));
                }
            }
        }
        if (!rule.hasLogicOperators()){
            return;
        }
        for (LogicRule subRule : rule.getRules()) {
            doToLogicRule(subRule, false);
        }
        rule.setExpression("");
    }

    private static String doPrettyFormat(LogicRule rule, int depth){
        if (!rule.hasLogicOperators()){
            return rule.getExpression();
        }
        StringBuilder sb = new StringBuilder();
        String tabs = getTabs(depth);
        List<LogicOperator> logicOperators = rule.getLogicOperators();
        List<LogicRule> rules = rule.getRules();
        int size = rules.size();
        for (int i = 0; i < size; i++) {
            LogicRule r = rules.get(i);
            String express = doPrettyFormat(r, depth + 1);
            sb.append("(").append(getTabs(depth + 1));
            sb.append(express);
            sb.append(tabs).append(")");
            if (i < size - 1) {
                sb.append(tabs).append(logicOperators.get(i)).append(tabs);
            }
        }
        return sb.toString();
    }

    private static String getTabs(int depth) {
        String str = "\n";
        for (int j = 0; j < depth; j++) {
            str += "\t";
        }
        return str;
    }
}
