package com.haizhi.graph.common.el;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by chengmo on 2017/12/6.
 */
public class ExpressionTest {

    @Test
    public void format(){
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("_id", 111);
        row.put("talkCount", 100);
        row.put("totalTalkTime", 200);

        String expression = "投资 ${talkCount}次   总金额${totalTalkTime}元 ${total}";

        Expression e = ExpressionFactory.createExpression(expression);
        if (e.validExpression()){
            System.out.println(e.format(row));
        }
    }
}
