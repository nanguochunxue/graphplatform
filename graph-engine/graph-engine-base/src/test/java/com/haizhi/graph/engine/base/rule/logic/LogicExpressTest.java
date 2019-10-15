package com.haizhi.graph.engine.base.rule.logic;

import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.util.FileUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengmo on 2018/7/23.
 */
public class LogicExpressTest {

    @Test
    public void example(){
        // LogicRule.json -> expression
        String json = FileUtils.readTxtFile("logic_rule1.json");
        String expression = LogicExpress.toExpression(json);
        System.out.println(expression);
        System.out.println();

        // expression -> LogicRule
        LogicRule rule = LogicExpress.toLogicRule(expression);
        JSONUtils.println(rule);
        System.out.println();

        // prettyFormat
        System.out.println(LogicExpress.prettyFormat(expression));
    }

    @Test
    public void exercise(){
        //String str = "(a or b) and (c or d) and @field(345.field)";
        String str = "(@field(1) > 10000) AND (@field(2) in (央企，国企)) OR (@yearAgo(@field(3)) < 10)";
        int length = str.length();
        int count = 0;
        String temp = "";
        List<String> rules = new ArrayList<>();
        int lastSubIndex = 0;
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            temp += Character.toLowerCase(ch);
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
            temp = temp.toLowerCase();
            if (temp.contains(" and ")) {
                rules.add(str.substring(lastSubIndex, i - 5 + 1));
                lastSubIndex = i + 1;
            } else if (temp.contains(" or ")) {
                rules.add(str.substring(lastSubIndex, i - 4 + 1));
                lastSubIndex = i + 1;
            } else {
                if (lastSubIndex > 0 && i == length - 1){
                    rules.add(str.substring(lastSubIndex, length));
                }
            }
        }
        System.out.println(rules);
    }
}
