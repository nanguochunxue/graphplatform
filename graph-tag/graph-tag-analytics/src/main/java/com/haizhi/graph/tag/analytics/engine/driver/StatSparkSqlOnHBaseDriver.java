package com.haizhi.graph.tag.analytics.engine.driver;

import com.haizhi.graph.engine.flow.sql.Projections;
import com.haizhi.graph.engine.flow.tools.hbase.HBaseContext;
import com.haizhi.graph.engine.flow.tools.hbase.HBaseScan;
import com.haizhi.graph.tag.analytics.engine.conf.DataSourceType;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTask;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTaskInput;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTaskSchema;
import com.haizhi.graph.tag.analytics.engine.driver.handler.FlowTaskHandler;
import com.haizhi.graph.tag.analytics.engine.driver.handler.StatResultHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/3/30.
 */
public class StatSparkSqlOnHBaseDriver extends SparkDriverBase {

    public static void main(String[] args) throws Exception {
        run(StatSparkSqlOnHBaseDriver.class, args);
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
        SparkSession spark = this.getOrCreate();
        HBaseContext hBaseContext = new HBaseContext(spark);

        /* Stage1 statistics */
        doRunFirstStage(task, spark, hBaseContext);

        /* Stage2 statistics */
        doRunSecondStage(task, spark, hBaseContext);
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private void doRunFirstStage(FlowTask task, SparkSession spark, HBaseContext hBaseContext) throws IOException {
        /* input */
        FlowTask.Stage stage = task.getStage(1);
        this.createDataFrames(stage, spark, hBaseContext);

        /* run sql */
        Dataset<Row> firstDS = spark.sql(stage.getInput().getSql());
        if (debug){
            System.out.println("firstDS.count()=" + firstDS.count());
        }
        firstDS.show(2);

        /* output */
        JavaRDD<Row> resultRdd = firstDS.toJavaRDD();
        FlowTaskSchema sch = FlowTaskHandler.getHBaseSchema(stage.getOutput());
        // persist
        long tagId = task.getTagId();
        String tableName = task.getGraph() + ":" + sch.getSchema();
        StatResultHandler.persistOnFirstStage(resultRdd, tagId, tableName);
    }

    private void doRunSecondStage(FlowTask task, SparkSession spark, HBaseContext hBaseContext) throws IOException {
        /* input */
        FlowTask.Stage stage = task.getStage(2);
        this.createDataFrames(stage, spark, hBaseContext);

        /* run sql */
        Dataset<Row> secondDS = spark.sql(stage.getInput().getSql());
        if (debug){
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

    private void createDataFrames(FlowTask.Stage stage, SparkSession spark, HBaseContext hBaseContext) throws IOException {
        FlowTaskInput input = stage.getInput();
        String graph = input.getGraph();
        for (FlowTaskSchema sch : input.getSchemas().values()) {
            if (sch.getSourceType() != DataSourceType.HBASE){
                continue;
            }
            String tableName = graph + ":" + sch.getSchema();
            List<StructField> structFields = new ArrayList<>();
            Map<String, String> fields = new LinkedHashMap<>();

            // rowKey
            structFields.add(DataTypes.createStructField(Projections.id, DataTypes.StringType, true));
            for (FlowTaskSchema.Field sf : sch.getFields().values()) {
                String fieldName = sf.getField();
                String structField = fieldName;
                if (!fieldName.contains(":")) {
                    fieldName = "objects:" + fieldName;
                }
                fields.put(fieldName, sf.getDataType());
                if (structField.contains(":")) {
                    structField = StringUtils.substringAfter(structField, ":");
                }
                structFields.add(DataTypes.createStructField(structField, DataTypes.StringType, true));
            }

            // StructType
            StructType schema = DataTypes.createStructType(structFields);

            // StructType.data
            HBaseScan scan = new HBaseScan(tableName);
            scan.setReduceKey(sch.getReduceKey());
            scan.setFields(fields);
            scan.setStartRow(sch.getStartRow());
            scan.setStopRow(sch.getStopRow());
            JavaRDD<Row> schemaRdd = hBaseContext.scanRDD2Row(scan);
            if (debug){
                System.out.println(">>>>>>table name=" + tableName);
                System.out.println(">>>>>>rows=" + schemaRdd.count());
                schemaRdd.take(2).forEach(row -> System.out.println(row.toString()));
            }

            // Temp view
            Dataset schemaDf = spark.createDataFrame(schemaRdd, schema);
            schemaDf.createOrReplaceTempView(sch.getSchema());
            schemaDf.printSchema();
        }
    }
}
