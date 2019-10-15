package com.haizhi.graph.engine.base.rule.express.function;

import com.ql.util.express.Operator;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by chengmo on 2018/6/8.
 */
public class SumFunction extends Operator {

    @Override
    public Object executeInner(Object[] list) throws Exception {
        BigDecimal r = BigDecimal.ZERO;
        for (int i = 0; i < list.length; i++) {
            Object obj = list[i];
            if (obj instanceof List) {
                List<Object> values = (List) obj;
                for (Object value : values) {
                    if (value instanceof BigDecimal) {
                        r = r.add((BigDecimal) value);
                    } else {
                        r = r.add(new BigDecimal(value.toString()));
                    }
                }
            }
        }
        return r;
    }
}
