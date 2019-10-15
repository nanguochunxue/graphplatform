package com.haizhi.graph.tag.analytics.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.lang3.StringUtils;

import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chengmo on 2018/4/9.
 */
public class SqlUtils {

    private static final GLog LOG = LogFactory.getLogger(SqlUtils.class);

    public static boolean isAggFunction(String func){
        return AGG_FUNCTIONS.containsKey(func.toLowerCase());
    }

    public static void println(String sql){
        System.out.println("sql>>>" + sql);
    }

    /**
     * Gets multi-value json string.
     * For Example: {"count$1$": "次数", "max$reg_amount$": "最大值"}
     *
     * @param sql
     * @return
     */
    public static String getMultiValueSchema(String sql){
        Map<String, Object> map = new HashMap<>();
        Set<String> projections = getProjections(sql);
        for (String projection : projections) {
            String func = StringUtils.substringBefore(projection, "(");
            if (!SqlUtils.isAggFunction(func)){
                continue;
            }
            String label = getAggFunctionLabel(func);
            map.put(projection, label);
        }
        return JSON.toJSONString(map);
    }

    /**
     * Gets projections by SQL.
     *
     * @param sql
     * @return
     */
    public static Set<String> getProjections(String sql){
        Set<String> projections = new LinkedHashSet<>();
        try {
            sql = escapeSQL(sql);
            CCJSqlParserManager parser = new CCJSqlParserManager();
            Select select = (Select) parser.parse(new StringReader(sql));
            PlainSelect plain = (PlainSelect) select.getSelectBody();

            // select.items
            List<SelectItem> selectItems = plain.getSelectItems();
            for (SelectItem selectItem : selectItems) {
                SelectExpressionItem item = (SelectExpressionItem)selectItem;
                String alias = item.getAlias() == null ? "": item.getAlias().getName();
                if (StringUtils.isNotBlank(alias)){
                    projections.add(alias);
                    continue;
                }
                Expression expr = item.getExpression();
                if (expr instanceof Column){
                    Column column = (Column)expr;
                    projections.add(column.getColumnName());
                }
                if (expr instanceof Function){
                    String str = expr.toString().toLowerCase();
                    projections.add(str.replaceAll("\\w+\\.", ""));
                }
            }
        } catch (JSQLParserException e) {
            LOG.error(e);
        }
        return projections;
    }

    /**
     * Gets all tables and fields by SQL.
     *
     * @param sql
     * @return
     */
    public static Map<String, Set<String>> getTableAndFields(String sql){
        Map<String, Set<String>> tableToFields = new LinkedHashMap<>();
        Map<String, String> aliasToTable = new LinkedHashMap<>();
        try {
            sql = escapeSQL(sql);
            CCJSqlParserManager parser = new CCJSqlParserManager();
            Select select = (Select) parser.parse(new StringReader(sql));
            PlainSelect plain = (PlainSelect) select.getSelectBody();

            // select.from.table
            Table table = (Table)plain.getFromItem();
            String fromTable = table.getName();
            tableToFields.put(fromTable, new HashSet<>());
            if (table.getAlias() != null){
                aliasToTable.put(table.getAlias().getName(), fromTable);
            }

            // join.table
            List<Join> joins = plain.getJoins();
            if (joins != null){
                for (Join join : joins) {
                    Table joinTable = (Table)join.getRightItem();
                    String joinTableName = joinTable.getName();
                    tableToFields.put(joinTableName, new HashSet<>());
                    aliasToTable.put(joinTable.getAlias().getName(), joinTableName);
                    ComparisonOperator operator = (ComparisonOperator) join.getOnExpression();

                    // left
                    Column left = (Column) operator.getLeftExpression();
                    String leftTableAlias = left.getTable().getName();
                    Set<String> set = tableToFields.get(aliasToTable.get(leftTableAlias));
                    set.add(left.getColumnName());
                    //right
                    Column right = (Column) operator.getRightExpression();
                    String rightTableAlias = right.getTable().getName();
                    set = tableToFields.get(aliasToTable.get(rightTableAlias));
                    set.add(right.getColumnName());
                }
            }

            // select.items
            List<SelectItem> selectItems = plain.getSelectItems();
            for (SelectItem selectItem : selectItems) {
                SelectExpressionItem item = (SelectExpressionItem)selectItem;
                Expression expr = item.getExpression();
                addField(expr, aliasToTable, tableToFields);
            }

            // where
            Expression where = plain.getWhere();
            if (where != null){
                addFieldByFunction(where.toString(), aliasToTable, tableToFields);
            }

            // group
            List<Expression> groupBy = plain.getGroupByColumnReferences();
            if (groupBy != null){
                for (Expression expr : groupBy) {
                    addField(expr, aliasToTable, tableToFields);
                }
            }
        } catch (JSQLParserException e) {
            LOG.error(e);
        }
        return tableToFields;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private static void addField(Expression expr, Map<String, String> aliasToTable, Map<String, Set<String>> tableToFields){
        if (expr instanceof Column){
            Column column = (Column)expr;
            String tableAlias = column.getTable().getName();
            Set<String> set = tableToFields.get(aliasToTable.get(tableAlias));
            set.add(column.getColumnName());
        }
        if (expr instanceof Function){
            addFieldByFunction(expr.toString(), aliasToTable, tableToFields);
        }
    }

    private static void addFieldByFunction(String expression, Map<String, String> aliasToTable, Map<String, Set<String>> tableToFields){
        Matcher matcher = Pattern.compile("\\w+\\.\\w+").matcher(expression);
        while (matcher.find()){
            String str = matcher.group();
            String tableAlias = StringUtils.substringBefore(str, ".");
            Set<String> set = tableToFields.get(aliasToTable.get(tableAlias));
            set.add(StringUtils.substringAfter(str, "."));
        }
    }

    private static String getAggFunctionLabel(String aggFunction){
        return AGG_FUNCTIONS.getOrDefault(aggFunction, "unknown");
    }

    private static String escapeSQL(String sql){
        sql = sql.replaceAll("rlike", "like");
        if (sql.toLowerCase().contains("union all")){
            sql = StringUtils.substringBefore(sql.toLowerCase(), "union all");
        }
        return sql;
    }

    private static final Map<String, String> AGG_FUNCTIONS = ImmutableMap.<String, String>builder()
            .put("count", "次数")
            .put("sum", "求和")
            .put("avg", "平均值")
            .put("min", "最小值")
            .put("max", "最大值")
            .put("stddev_pop", "标准差")
            .build();
}
