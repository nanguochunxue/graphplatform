package com.haizhi.graph.common.el;

import java.util.Map;

/**
 * Created by chengmo on 2017/12/6.
 */
public interface Expression {

    /**
     * Get expression.
     *
     * @return
     */
    String getExpression();


    /**
     *
     *
     * @return
     */
    boolean validExpression();

    /**
     * Expression format with ${}
     *
     * @param vars  Map context vars
     * @return
     */
    String format(Map<String, Object> vars);
}
