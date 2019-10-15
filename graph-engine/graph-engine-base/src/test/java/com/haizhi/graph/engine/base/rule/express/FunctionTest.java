package com.haizhi.graph.engine.base.rule.express;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/6/8.
 */
public class FunctionTest {

    @Test
    public void example() throws Exception{
        Map<String, Object> context = new HashMap<>();
        RuleExpressRunner runner = new RuleExpressRunner();

        String expr = "percent((max1(a) + min1(a)) / sum(a))";
        List<BigDecimal> list = new ArrayList<>();
        list.add(new BigDecimal(20000000000D));
        list.add(new BigDecimal(10000000000D));
        context.put("a", list);
        Object result = runner.execute(expr, context);
        System.out.println(result);//100%

        expr = "max1(a) + min1(a)";
        result = runner.execute(expr, context);
        System.out.println(NumberFormat.getNumberInstance().format(result));
    }

    @Test
    public void example1(){
        Map<String, Object> context = new HashMap<>();
        RuleExpressRunner runner = new RuleExpressRunner();
        String expr = "percent(sum(@field$te_invest$invest_amount$)/@field$Company$reg_capital$)";
        //String expr = "sum(@field$te_invest$invest_amount$) / @field$Company$reg_capital$";

        List<BigDecimal> list = new ArrayList<>();
        list.add(new BigDecimal(6.0E+8));
        //list.add(new BigDecimal(200000));
        context.put("@field$te_invest$invest_amount$", list);
        context.put("@field$Company$reg_capital$", new BigDecimal(2.0E+9));

        Object result = runner.execute(expr, context);
        System.out.println(result);
        //System.out.println(NumberFormat.getNumberInstance().format(result));
    }
}
