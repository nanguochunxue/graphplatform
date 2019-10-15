package com.haizhi.graph.tag.analytics.engine.driver.handler;

import com.haizhi.graph.tag.analytics.bean.Partitions;
import com.haizhi.graph.tag.analytics.bean.TagValue;
import com.haizhi.graph.tag.analytics.bean.TagValueDaily;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTaskSchema;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * Created by chengmo on 2018/4/23.
 */
public class HiveSqlBuilder {

    /**
     * @param sql
     * @param partitions
     * @return
     */
    public static String rebuild(String sql, Partitions partitions) {
        if (partitions == null || partitions.isEmpty()) {
            return sql;
        }
        String expr = getWhereExpression(partitions);
        if (sql.contains("union all")) {
            String[] clauses = sql.split("union all");
            StringBuilder sb = new StringBuilder();
            for (String clause : clauses) {
                if (clause.contains(" where ")) {
                    sb.append(clause.replace("where", expr + " and "));
                } else {
                    sb.append(clause + " " + expr);
                }
                sb.append(" union all ");
            }
            // delete " union all "
            sb = sb.delete(sb.length() - 11, sb.length());
            return sb.toString();
        } else {
            if (sql.contains(" where ")) {
                sql = sql.replace("where", expr + " and ");
            } else {
                sql = sql + " " + expr;
            }
        }
        return sql;
    }

    /**
     * Query all data if the partitions filter is empty.
     *
     * @param graph
     * @param sch
     * @param partitions
     * @return
     */
    public static String select(String graph, FlowTaskSchema sch, Partitions partitions) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        for (FlowTaskSchema.Field sf : sch.getFields().values()) {
            sql.append("t.").append(sf.getField()).append(",");
        }
        sql = sql.delete(sql.length() - 1, sql.length());
        String schemaName = sch.getSchema();
        String tableName = graph + "." + schemaName;
        sql.append(" from ").append(tableName).append(" t ");
        if (needsWherePartitions(schemaName)) {
            if (partitions != null && !partitions.isEmpty()) {
                sql.append(getWhereExpression(partitions));
            }
        }
        return sql.toString();
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private static boolean needsWherePartitions(String schemaName) {
        if (StringUtils.containsAny(schemaName, TagValue._schema, TagValueDaily._schema)) {
            return false;
        }
        return true;
    }

    private static String getWhereExpression(Partitions partitions) {
        StringBuilder sb = new StringBuilder();
        sb.append("where ");
        for (Partitions.Partition p : partitions.getPartitions().values()) {
            sb.append("t.").append(p.getName());

            // range
            if (p.isRange()) {
                String from = p.getFromRangeValue();
                String to = p.getToRangeValue();
                boolean fromFlag = StringUtils.isNotBlank(from);
                boolean toFlag = StringUtils.isNotBlank(to);
                if (fromFlag && !toFlag) {
                    sb.append(">").append("'").append(from).append("'");
                } else if (!fromFlag && toFlag) {
                    sb.append("<").append("'").append(to).append("'");
                } else if (fromFlag && toFlag) {
                    sb.append(">").append("'").append(from).append("'");
                    sb.append(" and ");
                    sb.append("<").append("'").append(to).append("'");
                }
                continue;
            }

            Set<String> values = p.getValues();
            if (values.size() == 0) {
                continue;
            }
            if (values.size() == 1) {
                sb.append("='").append(values.iterator().next()).append("'");
                continue;
            }
            sb.append(" in(");
            for (String value : values) {
                sb.append("'").append(value).append("',");
            }
            sb = sb.delete(sb.length() - 1, sb.length());
            sb.append(")");
        }
        return sb.toString();
    }
}
