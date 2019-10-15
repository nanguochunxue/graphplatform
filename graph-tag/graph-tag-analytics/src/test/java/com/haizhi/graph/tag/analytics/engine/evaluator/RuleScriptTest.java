package com.haizhi.graph.tag.analytics.engine.evaluator;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/3/17.
 */
public class RuleScriptTest {

    ExpressRunner runner = new ExpressRunner();

    @Test
    public void execute() throws Exception {
        DefaultContext<String, Object> context = new DefaultContext<>();
        Map<String, Object> row = new HashMap<>();
        row.put("reg_amount", 1500000000);
        context.put("Company", row);

        String source = "if (Company.reg_amount < 1000000000) {return 1} else {return 2}";
        Object result = runner.execute(source, context, null, false, true);
        System.out.println(result);
    }

    @Test
    public void execute1() throws Exception {
        DefaultContext<String, Object> context = new DefaultContext<>();
        Map<String, Object> row = new HashMap<>();
        row.put("count$1$", 6.0);
        context.put("SQL_40500151", row);

        row = new HashMap<>();
        row.put("company_key", "0286ed071e9f16c216ec62d064757db3");
        row.put("rank", 1);
        context.put("SQL_40500152", row);

        String source = "if (SQL_40500152.rank > 0 and SQL_40500152.rank <= 0.5*SQL_40500151.count$1$) {return 1} else {return 0}";
        Object result = runner.execute(source, context, null, false, true);
        System.out.println(result);
    }
}
