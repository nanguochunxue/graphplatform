package com.haizhi.graph.engine.base.rule.express;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/5/21.
 */
public class RuleExpressRunnerTest {

    @Test
    public void example(){
        Map<String, Object> context = new HashMap<>();
        RuleExpressRunner runner = new RuleExpressRunner();

        // example1
        String source = "if (Company.reg_amount < 1000000000) {return 1} else {return 2}";
        Map<String, Object> row = new HashMap<>();
        row.put("reg_amount", 1500000000);
        context.put("Company", row);
        Object result = runner.execute(source, context);
        System.out.println(result);

        // example2
        source = "count$reg_amount$+sum$reg_amount$";
        context.clear();
        context.put("count$reg_amount$", 100);
        context.put("sum$reg_amount$", 2000);
        result = runner.execute(source, context);
        System.out.println(result);
    }
}
