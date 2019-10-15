package com.haizhi.graph.tag.analytics.engine.driver;

import com.haizhi.graph.engine.flow.tools.hbase.HBaseContext;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTask;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTaskSchema;
import com.haizhi.graph.tag.analytics.engine.driver.handler.FlowTaskHandler;
import com.haizhi.graph.tag.analytics.engine.driver.handler.HiveSqlBuilder;
import com.haizhi.graph.tag.analytics.engine.driver.handler.StatResultHandler;
import com.haizhi.graph.tag.analytics.util.SqlUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;

/**
 * Created by chengmo on 2018/4/18.
 */
public class StatSparkSqlOnHiveDriver extends SparkDriverBase {

    public static void main(String[] args) throws Exception {
        run(StatSparkSqlOnHiveDriver.class, args);
    }

    /**
     * 统计默认按两个阶段处理，设计兼容多个阶段的扩展
     *
     * @param args
     * @throws Exception
     */
    @Override
    protected void run(String[] args) throws Exception {
        /* FLowTask */
        FlowTask task = this.getFlowTask(args);
        SparkSession spark = this.getOrCreate(true);
        HBaseContext hBaseContext = new HBaseContext(spark);

        /* Stage1 statistics */
        doRunFirstStage(task, spark, hBaseContext);

        /* Stage2 statistics */
        doRunSecondStage(task, spark, hBaseContext);
        spark.close();
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private void doRunFirstStage(FlowTask task, SparkSession spark, HBaseContext hBaseContext) throws IOException {
        /* input */
        FlowTask.Stage stage = task.getStage(1);

        /* run sql */
        spark.sql("USE " + task.getGraph());
        SqlUtils.println("USE " + task.getGraph());
        String sql = stage.getInput().getSql();
        sql = HiveSqlBuilder.rebuild(sql, task.getPartitions());
        Dataset<Row> firstDS = spark.sql(sql);
        SqlUtils.println(sql);
        if (debug) {
            System.out.println("firstDS.count()=" + firstDS.count());
        }
        firstDS.show(2);

        /* output */
        JavaRDD<Row> resultRdd = firstDS.toJavaRDD();
        if (resultRdd.isEmpty()) {
            System.out.println("ResultRdd is empty, return.");
            return;
        }
        // persist
        FlowTaskSchema sch = FlowTaskHandler.getHBaseSchema(stage.getOutput());
        long tagId = task.getTagId();
        String tableName = task.getGraph() + ":" + sch.getSchema();
        StatResultHandler.persistOnFirstStage(resultRdd, tagId, tableName);
    }

    private void doRunSecondStage(FlowTask task, SparkSession spark, HBaseContext hBaseContext) throws IOException {
        /* input */
        FlowTask.Stage stage = task.getStage(2);

        /* run sql */
        String sql = stage.getInput().getSql();
        Dataset<Row> secondDS = spark.sql(sql);
        SqlUtils.println(sql);
        if (debug) {
            System.out.println("secondDS.count()=" + secondDS.count());
        }
        secondDS.show(2);

        /* output */
        JavaRDD<Row> resultRdd = secondDS.toJavaRDD();
        FlowTaskSchema sch = FlowTaskHandler.getHBaseSchema(stage.getOutput());
        // persist
        long tagId = task.getTagId();
        String tableName = task.getGraph() + ":" + sch.getSchema();
        StatResultHandler.persistOnSecondStage(resultRdd, tagId, tableName, task);
    }
}
