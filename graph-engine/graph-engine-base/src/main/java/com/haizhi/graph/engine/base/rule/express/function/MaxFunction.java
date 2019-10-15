package com.haizhi.graph.engine.base.rule.express.function;

import com.ql.util.express.Operator;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.Objects;

/**
 * Created by chengmo on 2018/6/8.
 */
public class MaxFunction extends Operator {

    @Override
    public Object executeInner(Object[] list) throws Exception {
        double r = Double.MIN_VALUE;
        for (int i = 0; i < list.length; i++) {
            Object obj = list[i];
            if (obj instanceof List){
                List<Object> values = (List) obj;
                for (Object value : values) {
                    String str = Objects.toString(value, "0");
                    double d = NumberUtils.toDouble(str, 0);
                    r = Math.max(r, d);
                }
            }
        }
        return r;
    }
}
