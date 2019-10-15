package com.haizhi.graph.tag.analytics.engine.driver;

import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.engine.flow.tools.hbase.HBaseContext;
import com.haizhi.graph.engine.flow.tools.hbase.HBaseScan;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTask;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTaskInput;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTaskSchema;
import com.haizhi.graph.tag.analytics.engine.driver.handler.LogicEvaluator;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/3/14.
 */
public class LogicSparkOnHBaseDriver extends SparkDriverBase {

    public static void main(String[] args) throws Exception {
        run(LogicSparkOnHBaseDriver.class, args);
    }

    /**
     * @param args args[0]      FlowTask.id
     * @throws Exception
     */
    @Override
    protected void run(String[] args) throws Exception {
        /* FlowTask */
        FlowTask task = this.getFlowTask(args);
        SparkSession spark = this.getOrCreate();
        HBaseContext hBaseContext = new HBaseContext(spark);

        /* 1.union rdd */
        JavaPairRDD<String, String> rootRdd = this.unionAll(task, spark, hBaseContext);

        /* 2.reduce by key */
        JavaPairRDD<String, String> resultRdd = this.reduceByKey(rootRdd);

        /* 3.logic evaluator */
        LogicEvaluator.evaluateAndPersist(resultRdd, task);
        spark.close();
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private JavaPairRDD<String, String> unionAll(FlowTask task, SparkSession spark, HBaseContext hBaseContext) throws
            IOException {
        String graph = task.getGraph();
        JavaPairRDD<String, String> rootRdd = null;
        FlowTask.Stage stage = task.getStage(1);
        FlowTaskInput input = stage.getInput();
        for (FlowTaskSchema sch : input.getSchemas().values()) {
            String tableName = graph + ":" + sch.getSchema();
            Map<String, String> fields = new LinkedHashMap<>();
            for (FlowTaskSchema.Field sf : sch.getFields().values()) {
                String fieldName = sf.getField();
                if (!fieldName.contains(":")) {
                    fieldName = "objects:" + fieldName;
                }
                fields.put(fieldName, sf.getDataType());
            }
            System.out.println("tableName=" + tableName);

            // HBaseContext
            HBaseScan scan = new HBaseScan(tableName);
            scan.setReduceKey(sch.getReduceKey());
            scan.setFields(fields);
            scan.setStartRow(sch.getStartRow());
            scan.setStopRow(sch.getStopRow());
            JavaPairRDD<String, String> rdd = hBaseContext.scanRDD2JSON(scan);
            if (!debug) {
                System.out.println("rdd.count()=" + rdd.count());
            }
            if (rootRdd == null) {
                rootRdd = rdd;
                continue;
            }
            rootRdd = rootRdd.union(rdd);
        }
        return rootRdd;
    }

    private JavaPairRDD<String, String> reduceByKey(JavaPairRDD<String, String> rootRdd) {
        return rootRdd.reduceByKey((v1, v2) -> v1 + FKeys.SEPARATOR_003 + v2);
    }
}
