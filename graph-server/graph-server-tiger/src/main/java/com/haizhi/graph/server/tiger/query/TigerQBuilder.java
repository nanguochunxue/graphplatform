package com.haizhi.graph.server.tiger.query;

import java.util.Map;

/**
 * Created by tanghaiyang on 2019/3/15.
 */
public interface TigerQBuilder extends QBuilder{

    void buildFilter(StringBuilder expression, Map<String, Object> filter);

    String toRangeExpression(StringBuilder expression);

    String toTermExpression(StringBuilder expression);

    String toLikeExpression(StringBuilder expression);

    String toNotExpression(StringBuilder expression);

    String toIsNullExpression(StringBuilder expression);

}
