package com.haizhi.graph.engine.base.rule.express.function;

import com.ql.util.express.Operator;

import java.text.NumberFormat;

/**
 * Created by chengmo on 2018/6/8.
 */
public class PercentFunction extends Operator {

    @Override
    public Object executeInner(Object[] list) throws Exception {
        String r = "0.00%";
        for (int i = 0; i < list.length; i++) {
            Object obj = list[i];
            NumberFormat nf = NumberFormat.getPercentInstance();
            nf.setMinimumFractionDigits(2);
            r = nf.format(obj);
            break;
        }
        return r;
    }
}
