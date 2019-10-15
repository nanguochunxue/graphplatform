package com.haizhi.graph.server.tiger.query.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.tiger.query.TigerQBuilder;
import lombok.Data;

import java.util.List;
import java.util.Map;

import static com.haizhi.graph.server.tiger.constant.FilterKeys.*;

/**
 * Created by tanghaiyang on 2019/3/14.
 */
@Data
public class TigerQBuilderImpl implements TigerQBuilder {

    private static final GLog LOG = LogFactory.getLogger(TigerQBuilderImpl.class);

    protected String schema;
    protected String schemaType;
    protected String type;
    protected String field;
    protected String fieldType;
    protected List<Object> values;

    @Override
    public void toExpression(StringBuilder expression){
        switch (type){
            case FILTER_OPERATOR_IN:
                toTermExpression(expression);
                break;

            case FILTER_OPERATOR_RANGE:
                toRangeExpression(expression);
                break;

            case FILTER_OPERATOR_LIKE:
                // TODO: do something, mark at 2019/3/14 17:41
                break;

            default:
                break;
        }
    }

    @Override
    public void buildFilter(StringBuilder expression, Map<String, Object> filter){
        if(filter.containsKey(FILTER_LOGIC_OPERATORS) && filter.containsKey(FILTER_LOGIC_RULES)) {
            JSONArray logicOperators = (JSONArray)filter.get(FILTER_LOGIC_OPERATORS);
            JSONArray rules = (JSONArray)filter.get(FILTER_LOGIC_RULES);
            expression.append(FILTER_LEFT_BRACKET);
            for (int i = 0; i < rules.size(); i++) {
                JSONObject rule = (JSONObject) rules.get(i);
                if (i < rules.size() - 1) {
                    buildRule(expression, rule);
                    expression.append(FILTER_BLANK).append(logicOperators.get(i)).append(FILTER_BLANK);
                } else {
                    buildRule(expression, rule);
                }
            }
            expression.append(FILTER_RIGHT_BRACKET);
        }
    }

    @SuppressWarnings("all")
    @Override
    public String toRangeExpression(StringBuilder expression) {
        String alias;
        switch (schemaType){
            case FILTER_SCHEMA_VERTEX:
                alias = FILTER_ALIAS_VERTEX;
                break;
            case FILTER_SCHEMA_EDGE:
                alias = FILTER_ALIAS_EDGE;
                break;
            default:
                return null;
        }
        expression.append(FILTER_LEFT_BRACKET)
                .append(alias)
                .append(FILTER_DOT).append(FILTER_TYPE)
                .append(FILTER_OPERATOR_EQUAL)
                .append(FILTER_QUOTE).append(schema).append(FILTER_QUOTE)
                .append(FILTER_BLANK).append(FILTER_OPERATOR_AND).append(FILTER_BLANK)
                .append(FILTER_LEFT_BRACKET);
        for(int i = 0; i < values.size(); i++){
            Object o = values.get(i);
            if(! (o instanceof Map)) {
                LOG.error("object is not instanceof Map");
                continue;
            }
            Map<String, Object> value = (Map<String, Object>)values.get(i);
            buildRange(expression, alias, value);
            if( i < values.size() - 1) {
                expression.append(FILTER_BLANK).append(FILTER_OPERATOR_OR).append(FILTER_BLANK);
            }
        }
        expression.append(FILTER_RIGHT_BRACKET).append(FILTER_RIGHT_BRACKET);
        return expression.toString();
    }

    @Override
    public String toTermExpression(StringBuilder expression) {
        expression.append(FILTER_LEFT_BRACKET);
        switch (schemaType){
            case FILTER_SCHEMA_VERTEX:
                expression.append(FILTER_ALIAS_VERTEX).append(FILTER_DOT)
                        .append(FILTER_TYPE).append(FILTER_OPERATOR_EQUAL)
                        .append(FILTER_QUOTE).append(schema).append(FILTER_QUOTE)
                        .append(FILTER_BLANK).append(FILTER_OPERATOR_AND).append(FILTER_BLANK)
                        .append(FILTER_LEFT_BRACKET);

                for(int i = 0; i < values.size(); i++) {
                    Object value = values.get(i);
                    expression.append(FILTER_ALIAS_VERTEX).append(FILTER_DOT)
                            .append(field)
                            .append(FILTER_OPERATOR_EQUAL);

                    switch (fieldType.toLowerCase()) {
                        case "string":
                            expression.append(FILTER_QUOTE).append(value).append(FILTER_QUOTE);
                            break;
                        case "int":
                            expression.append(value);
                    }
                    if (i < values.size() - 1) {
                        expression.append(FILTER_BLANK).append(FILTER_OPERATOR_OR).append(FILTER_BLANK);
                    }
                }
                expression.append(FILTER_RIGHT_BRACKET);
                break;
            case FILTER_SCHEMA_EDGE:
                break;
            default:
                break;
        }
        expression.append(FILTER_RIGHT_BRACKET);
        return expression.toString();
    }

    @Override
    public String toLikeExpression(StringBuilder expression) {
        return null;
    }

    @Override
    public String toNotExpression(StringBuilder expression) {
        return null;
    }

    @Override
    public String toIsNullExpression(StringBuilder expression) {
        return null;
    }


    ///////////////////////
    // private functions
    ///////////////////////
    private void buildRule(StringBuilder expression, Map<String, Object> rule){
        if(rule.containsKey(FILTER_LOGIC_OPERATORS) && rule.containsKey(FILTER_LOGIC_RULES)) {
            buildFilter(expression, rule);
        }else {
            TigerQBuilderImpl tigerQBuilderImpl = JSON.parseObject(JSON.toJSONString(rule), TigerQBuilderImpl.class);
            tigerQBuilderImpl.toExpression(expression);
        }
    }

    private void buildRange(StringBuilder expression, String alias, Map<String, Object> value){
        boolean includeLower = (Boolean) value.get(FILTER_KEY_LOWER);
        boolean includeUpper = (Boolean) value.get(FILTER_KEY_UPPER);
        Integer from = (Integer)value.get(FILTER_KEY_FROM);
        Integer to = (Integer)value.get(FILTER_KEY_TO);
        expression.append(FILTER_LEFT_BRACKET);
        expression.append(alias).append(FILTER_DOT).append(field);
        if(includeLower) {
            expression.append(FILTER_OPERATOR_MORE_EQUAL);
        }else {
            expression.append(FILTER_OPERATOR_MORE);
        }
        expression.append(from).append(FILTER_BLANK).append(FILTER_OPERATOR_AND).append(FILTER_BLANK);
        expression.append(alias).append(FILTER_DOT).append(field);
        if(includeUpper){
            expression.append(FILTER_OPERATOR_LESS_EQUAL);
        }else {
            expression.append(FILTER_OPERATOR_LESS);
        }
        expression.append(to);
        expression.append(FILTER_RIGHT_BRACKET);
    }

}
