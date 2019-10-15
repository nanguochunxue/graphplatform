package com.haizhi.graph.server.api.gdb.search.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.gdb.search.bean.Operator;
import com.haizhi.graph.server.api.gdb.search.bean.ParseFields;
import com.haizhi.graph.server.api.gdb.search.query.*;
import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengmo on 2018/1/25.
 */
public abstract class AbstractQueryParser implements QueryParser {

    private static final GLog LOG = LogFactory.getLogger(AbstractQueryParser.class);

    /* Flag character */
    protected static final String F001 = "\001";

    protected static final String BL = " ";
    protected static final String AND = Operator.AND.get();
    protected static final String OR = Operator.OR.get();
    protected static final String TRUE = Operator.TRUE.get();

    @Override
    public String toQuery(XContentBuilder builder) {
        try {
            return this.parseObjectTree(builder.rootObject());
        } catch (Exception e) {
            LOG.error("to query error.", e);
        }
        return "";
    }

    @Override
    public String toQueryExpression(XContentBuilder builder) {
        try {
            JSONObject graph = builder.rootObject().getJSONObject(GraphQBuilder.NAME);
            JSONObject query = graph.getJSONObject(GraphQBuilder.QUERY_FIELD);
            return parseGraphQuery(query);
        } catch (Exception e) {
            LOG.error("to query expression error.", e);
        }
        return "";
    }

    @Override
    public List<String> toMultiQuery(XContentBuilder builder)
    {
        //default empty implementation
        return null;
    }

    ///////////////////////
    // protected functions
    ///////////////////////
    protected String parseGraph(JSONObject object) {
        try {
            return doQuery(object);
        } catch (Exception e) {
            LOG.error("do query error.", e);
        }
        return "";
    }

    protected abstract String doQuery(JSONObject object);

    protected String parseObjectTree(JSONObject object) {
        if(object == null) return "";
        QBuilderType type = QBuilderType.fromName(XContentBuilder.getFirstKey(object));
        switch (type) {
            case GRAPH:
                return parseGraph(object);
            case BOOL:
                return parseBool(object);
            case VERTEX:
                return parseVertex(object);
            case EDGE:
                return parseEdge(object);
            case TERM:
                return parseTerm(object);
            case TERMS:
                return parseTerms(object);
            case LIKE:
                return parseLike(object);
            case IS_NULL:
                return parseIsNull(object);
            case RANGE:
                return parseRange(object);
        }
        return "";
    }

    protected String parseGraphQuery(JSONObject query) {
        String expr = parseObjectTree(query);
        expr = prettyFormat(expr);
        return expr;
    }

    protected String prettyFormat(String expression) {
        if (expression.contains("\n\t")) {
            return expression;
        }
        StringBuilder sb = new StringBuilder();
        int length = expression.length();
        int count = 0;
        String temp = "";
        for (int i = 0; i < length; i++) {
            char ch = expression.charAt(i);
            temp += ch;
            if (temp.length() == 8) {
                temp = temp.substring(1);
            }
            if (ch == '(') {
                count++;
                String tabs = getTabs(count - 1);
                if (temp.contains(") and (")) {
                    sb.replace(sb.length() - 5, sb.length() - 4, tabs);
                    sb.replace(sb.length() - 1, sb.length(), tabs);
                }
                if (temp.contains(") or (") && !temp.contains("((")) {
                    sb.replace(sb.length() - 4, sb.length() - 3, tabs);
                    sb.replace(sb.length() - 1, sb.length(), tabs);
                }
                sb.append(ch).append(getTabs(count));
                continue;
            }
            if (ch == ')') {
                count--;
                String tabs = getTabs(count);
                sb.append(tabs).append(ch);
                continue;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    protected String getTabs(int repeatCount) {
        String str = "\n";
        for (int j = 0; j < repeatCount; j++) {
            str += "\t";
        }
        return str;
    }

    protected String parseBool(JSONObject object) {
        JSONObject bool = object.getJSONObject(BoolQBuilder.NAME);
        JSONArray mustClauses = bool.getJSONArray(BoolQBuilder.MUST);
        JSONArray mustNotClauses = bool.getJSONArray(BoolQBuilder.MUST_NOT);
        JSONArray shouldClauses = bool.getJSONArray(BoolQBuilder.SHOULD);

        StringBuilder builder = new StringBuilder();

        // must
        parseBoolOperator(mustClauses, AND, builder);
        // should
        parseBoolOperator(shouldClauses, OR, builder);
        // mustNot
        parseBoolMustNot(mustNotClauses, builder);

        return builder.toString();
    }

    protected void parseBoolMustNot(JSONArray clauses, StringBuilder builder) {
        int size = clauses.size();
        if (size == 0) {
            return;
        }
        builder.append(TRUE).append(BL).append(AND).append(BL).append("(");
        for (int i = 0; i < size; i++) {
            JSONObject clause = clauses.getJSONObject(i);
            String expr = parseObjectTree(clause);

            // inverse conditional operators
            expr = inverseMustNot(Operator.EQUALS, Operator.NOT_EQUALS, expr);
            expr = inverseMustNot(Operator.GREATER_THAN, Operator.LESS_THAN, expr);
            expr = inverseMustNot(Operator.GREATER_THAN_OR_EQUALS, Operator.LESS_THAN_OR_EQUALS, expr);
            expr = inverseMustNot(Operator.LIKE, Operator.NOT_LIKE, expr);
            expr = inverseMustNot(Operator.BETWEEN, Operator.NOT_BETWEEN, expr);
            expr = inverseMustNot(Operator.IN, Operator.NOT_IN, expr);
            expr = inverseMustNot(Operator.IS_NULL, Operator.IS_NOT_NULL, expr);
            expr = expr.replaceAll(F001, "");

            if (StringUtils.contains(expr, Operator.BETWEEN.get() + BL)) {
                expr = expr.replaceAll(Operator.BETWEEN.get(), Operator.BETWEEN.get() + F001);
                builder.append("(").append(expr).append(")");
            } else {
                builder.append(expr);
            }
            if (i != size - 1) {
                builder.append(BL).append(AND).append(BL);
            }
        }
        builder.append(")");
    }

    protected String inverseMustNot(Operator op1, Operator op2, String expr) {
        expr = expr.replaceAll(op1.get(), op2.get() + F001);
        if (expr.contains(op2.get()) && !expr.contains(op2.get() + F001)) {
            expr = expr.replaceAll(op2.get(), op1.get());
        }
        return expr;
    }

    protected void parseBoolOperator(JSONArray clauses, String logicOperator, StringBuilder builder) {
        int size = clauses.size();
        if (size == 0) {
            return;
        }
        builder.append("(");
        for (int i = 0; i < size; i++) {
            JSONObject clause = clauses.getJSONObject(i);
            String expr = parseObjectTree(clause);
            builder.append(expr);
            if (i != size - 1) {
                builder.append(BL).append(logicOperator).append(BL);
            }
        }
        builder.append(")");
    }

    protected String parseVertex(JSONObject object) {
        return parseGraphTable(object, VertexQBuilder.NAME);
    }

    protected String parseEdge(JSONObject object) {
        return parseGraphTable(object, EdgeQBuilder.NAME);
    }

    protected String parseGraphTable(JSONObject object, String builderName) {
        JSONObject table = object.getJSONObject(builderName);
        String tableName = new ArrayList<>(table.keySet()).get(1);
        String tableAlias = table.getString(ParseFields.ALIAS_FIELD);
        String expression = parseObjectTree(table.getJSONObject(tableName));
        expression = expression.replaceAll("\\$", tableAlias);
        return expression;
    }

    protected String parseTerm(JSONObject object) {
        JSONObject field = object.getJSONObject(TermQBuilder.NAME);
        String fieldName = XContentBuilder.getFirstKey(field);
        JSONObject value = field.getJSONObject(fieldName);
        Object fieldValue = value.get(TermQBuilder.VALUE_FIELD);

        // $.fieldName==fieldValue
        return "$." + fieldName + Operator.EQUALS.get() + fieldValue;
    }

    protected String parseTerms(JSONObject object) {
        JSONObject field = object.getJSONObject(TermsQBuilder.NAME);
        String fieldName = XContentBuilder.getFirstKey(field);
        String values = stringValue(field.getJSONArray(fieldName));

        // $.fieldName==fieldValue
        return "$." + fieldName + BL + Operator.IN.get() + BL + values;
    }

    protected String parseLike(JSONObject object) {
        JSONObject field = object.getJSONObject(LikeQBuilder.NAME);
        String fieldName = XContentBuilder.getFirstKey(field);
        JSONObject value = field.getJSONObject(fieldName);
        Object fieldValue = value.get(TermQBuilder.VALUE_FIELD);

        // $.fieldName like fieldValue
        return "$." + fieldName + Operator.LIKE.get() + "\"" + fieldValue + "\"";
    }

    protected String parseIsNull(JSONObject object) {
        JSONObject field = object.getJSONObject(IsNullQBuilder.NAME);
        String table = field.getString(IsNullQBuilder.TABLE_FIELD);
        String fieldName = field.getString(IsNullQBuilder.FIELD_FIELD);

        // $.fieldName is null
        return table + "." + fieldName + BL + Operator.IS_NULL.get();
    }

    protected String parseRange(JSONObject object) {
        JSONObject field = object.getJSONObject(RangeQBuilder.NAME);
        String fieldName = XContentBuilder.getFirstKey(field);
        JSONObject params = field.getJSONObject(fieldName);
        Object from = params.get(RangeQBuilder.FROM_FIELD);
        Object to = params.get(RangeQBuilder.TO_FIELD);
        boolean includeLower = params.getBooleanValue(RangeQBuilder.INCLUDE_LOWER_FIELD);
        boolean includeUpper = params.getBooleanValue(RangeQBuilder.INCLUDE_UPPER_FIELD);

        String expr = "$." + fieldName;
        if (from == null && to == null) {
            return expr;
        }
        // between(>= and <=)
        if (from != null && to != null) {
            StringBuilder sb = new StringBuilder("(");
            if (includeLower){
                sb.append(expr);
                sb.append(Operator.GREATER_THAN_OR_EQUALS.get());
                sb.append(from);
            } else {
                sb.append(expr);
                sb.append(Operator.GREATER_THAN.get());
                sb.append(from);
            }
            sb.append(BL).append("and").append(BL);
            if (includeUpper){
                sb.append(expr);
                sb.append(Operator.LESS_THAN_OR_EQUALS.get());
                sb.append(to);
            } else {
                sb.append(expr);
                sb.append(Operator.LESS_THAN.get());
                sb.append(to);
            }
            sb.append(")");
            return sb.toString();
        }
        // >=,>
        if (from != null) {
            if (includeLower) {
                return expr + Operator.GREATER_THAN_OR_EQUALS.get() + from;
            }
            return expr + Operator.GREATER_THAN.get() + from;
        }
        // <=,<
        if (to != null) {
            if (includeUpper) {
                return expr + Operator.LESS_THAN_OR_EQUALS.get() + to;
            }
            return expr + Operator.LESS_THAN.get() + to;
        }
        return expr;
    }

    protected static String stringValue(JSONArray values) {
        if (values.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder("(");
        for (Object o : values) {
            sb.append("\"").append(o).append("\"").append(",");
        }
        sb = sb.delete(sb.length() - 1, sb.length());
        sb.append(")");
        return sb.toString();
    }
}
