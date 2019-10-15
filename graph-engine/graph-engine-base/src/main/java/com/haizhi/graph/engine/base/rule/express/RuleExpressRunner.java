package com.haizhi.graph.engine.base.rule.express;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.engine.base.rule.Rule;
import com.haizhi.graph.engine.base.rule.express.function.*;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

import java.util.Map;

/**
 * Created by chengmo on 2018/5/21.
 */
public class RuleExpressRunner {
    private static final GLog LOG = LogFactory.getLogger(RuleExpressRunner.class);
    private static ExpressRunner runner = new ExpressRunner(true, false);

    static {
        runner.addFunction(Function.MAX.code(), new MaxFunction());
        runner.addFunction(Function.MIN.code(), new MinFunction());
        runner.addFunction(Function.SUM.code(), new SumFunction());
        runner.addFunction(Function.PERCENT.code(), new PercentFunction());
    }

    /**
     * @param source
     * @param context
     * @return
     */
    public Object execute(String source, Map<String, Object> context){
        Object result = Rule.INVALID_RESULT;
        DefaultContext<String, Object> ctx = new DefaultContext<>();
        ctx.putAll(context);
        try {
            result = runner.execute(source, ctx, null, false, true);
        } catch (Exception e) {
            LOG.error(e);
        }
        return result;
    }
}
