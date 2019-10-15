package com.haizhi.graph.engine.flow.action;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;

/**
 * Created by chengmo on 2018/3/27.
 */
public class SparkSQLOnHiveMain {

    /**
     * local 本地单线程
     * local[K] 本地多线程（指定K个内核）
     * local[*] 本地多线程（指定所有可用内核）
     * spark://HOST:PORT 连接到指定的 Spark standalone cluster master，需要指定端口。
     * mesos://HOST:PORT 连接到指定的 Mesos 集群，需要指定端口。
     * yarn-client客户端模式 连接到 YARN 集群。需要配置 HADOOP_CONF_DIR。
     * yarn-cluster集群模式 连接到 YARN 集群 。需要配置 HADOOP_CONF_DIR。
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        SparkSession spark = SparkSession.builder()
                .master("local[*]")
                .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .config("spark.rdd.compress", "true")
                //.config("hive.metastore.uris", "hadoop01.sz.haizhi.com:9083")
                .enableHiveSupport()
                .getOrCreate();
        Dataset<Row> nameDf = null;

        /** json */
        // 1491062400
        nameDf = spark.sql("SELECT get_json_object('{\"a\":\"b\"}', '$.a')");
        nameDf.show();
        String json = "{\"count(1)\":2,\"min(reg_amount)\":1.458E9}";
        // AS count(1) 报错
        nameDf = spark.sql("SELECT " +
                "count(get_json_object('" + json + "', '$.count(1)')) AS count," +
                "sum(get_json_object('" + json + "', '$.min(reg_amount)')) AS sum");
        /*nameDf = spark.sql("SELECT " +
                "get_json_object('" + json + "', '$.count(1)')");*/
        nameDf.show();
        System.out.println("******************************************************");

        nameDf = spark.sql("CREATE DATABASE IF NOT EXISTS crm_dev");
        nameDf.show();
        nameDf = spark.sql("USE crm_dev");
        nameDf.show();
        String script = "CREATE EXTERNAL TABLE IF NOT EXISTS tag_value_daily(key string,tagId string,objectKey " +
                "string,dataType string,statTime string,value string) " +
                "STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' " +
                "WITH SERDEPROPERTIES ('hbase.columns.mapping'=':key,objects:tagId,objects:objectKey," +
                "objects:dataType,objects:statTime,objects:value') " +
                "TBLPROPERTIES('hbase.table.name'='crm_dev:tag_value_daily','hbase.mapred.output" +
                ".outputtable'='crm_dev:tag_value_daily')";
        spark.sql(script);
        nameDf = spark.sql("select * from crm_dev.company t limit 1");
        nameDf.show();
    }
}
