package com.haizhi.graph.tag.analytics.engine.driver;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.engine.flow.util.DataUtils;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTask;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTaskInput;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTaskSchema;
import com.haizhi.graph.tag.analytics.engine.driver.handler.HiveSqlBuilder;
import com.haizhi.graph.tag.analytics.engine.driver.handler.LogicEvaluator;
import com.haizhi.graph.tag.analytics.util.SqlUtils;
import com.haizhi.graph.tag.core.domain.TagSchemaType;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by chengmo on 2018/3/14.
 */
public class LogicSparkOnHiveDriver extends SparkDriverBase {

    public static void main(String[] args) throws Exception {
        run(LogicSparkOnHiveDriver.class, args);
    }

    /**
     * @param args args[0]      FlowTask.id
     * @throws Exception
     */
    @Override
    protected void run(String[] args) throws Exception {
        /* FlowTask */
        FlowTask task = this.getFlowTask(args);
        SparkSession spark = this.getOrCreate(true);

        /* Global cache */
        Map<String, String> globalCache = new HashMap<>();

        /* 1.union rdd */
        JavaPairRDD<String, String> rootRdd = this.unionAll(task, spark, globalCache);

        /* 2.reduce by key */
        JavaPairRDD<String, String> resultRdd = this.reduceByKey(rootRdd);

        /* 3.logic evaluator */
        LogicEvaluator.evaluateAndPersist(resultRdd, task, globalCache);
        spark.close();
    }

    ///////////////////////
    // private functions
    ///////////////////////
    @SuppressWarnings("unchecked")
    private JavaPairRDD<String, String> unionAll(FlowTask task, SparkSession spark, Map<String, String> globalCache)
            throws IOException {
        String graph = task.getGraph();
        JavaPairRDD<String, String> rootRdd = null;
        FlowTask.Stage stage = task.getStage(1);
        FlowTaskInput input = stage.getInput();
        spark.sql("USE " + graph);
        SqlUtils.println("USE " + graph);
        for (FlowTaskSchema sch : input.getSchemas().values()) {
            String tableName = graph + "." + sch.getSchema();
            String sql;
            if (sch.getSchemaType().equals(TagSchemaType.SQL.name())) {
                sql = HiveSqlBuilder.rebuild(sch.getSql(), task.getPartitions());
            } else {
                // TODO: 如果是增量，如果只有tag_value表，则需要按照时间过滤tag_value.updateTime > 前一天
                sql = HiveSqlBuilder.select(graph, sch, task.getPartitions());
            }
            if (StringUtils.isBlank(sql)) {
                continue;
            }
            SqlUtils.println(sql);

            // query
            Dataset<Row> tableDS = null;
            try {
                tableDS = spark.sql(sql.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (tableDS == null) {
                continue;
            }
            JavaRDD<Row> tableRdd = tableDS.toJavaRDD();
            tableRdd.take(2).forEach(row -> System.out.println(row.toString()));

            String reduceKey = sch.getReduceKey();
            if (SqlUtils.isAggFunction(reduceKey)) {
                StringBuilder text = new StringBuilder();
                text.append(tableName).append(FKeys.SEPARATOR_002);
                Map<String, Object> rowMap = new HashMap<>();
                tableRdd.take(1).forEach(row -> {
                    for (FlowTaskSchema.Field sf : sch.getFields().values()) {
                        String field = sf.getField();
                        Object value = row.getAs(sf.getField());
                        Object objValue = DataUtils.parseObject(Objects.toString(value, null), sf.getDataType());
                        field = field.replaceAll("\\(|\\)", "\\$");
                        rowMap.put(field, objValue);
                    }
                });
                text.append(JSON.toJSONString(rowMap));
                globalCache.put(tableName, text.toString());
                continue;
            }
            JavaPairRDD<String, String> pairRDD = tableRdd.mapToPair(row -> {
                String key = row.getAs(reduceKey);
                StringBuilder text = new StringBuilder();
                text.append(tableName).append(FKeys.SEPARATOR_002);
                Map<String, Object> rowMap = new HashMap<>();
                for (FlowTaskSchema.Field sf : sch.getFields().values()) {
                    String field = sf.getField();
                    Object value = row.getAs(field);
                    Object objValue = DataUtils.parseObject(Objects.toString(value, null), sf.getDataType());
                    String func = StringUtils.substringBefore(field, "(");
                    if (SqlUtils.isAggFunction(func)) {
                        field = field.replaceAll("\\(|\\)", "\\$");
                    }
                    rowMap.put(field, objValue);
                }
                text.append(JSON.toJSONString(rowMap));
                return new Tuple2(key, text.toString());
            });

            if (!debug) {
                System.out.println("rdd.count()=" + pairRDD.count());
            }
            if (rootRdd == null) {
                rootRdd = pairRDD;
                continue;
            }
            rootRdd = rootRdd.union(pairRDD);
        }
        System.out.println(globalCache);
        return rootRdd;
    }

    private JavaPairRDD<String, String> reduceByKey(JavaPairRDD<String, String> rootRdd) {
        if (rootRdd == null){
            return rootRdd;
        }
        return rootRdd.reduceByKey((v1, v2) -> {
            String result = v1 + FKeys.SEPARATOR_003 + v2;
            return result;
        });
    }
}
