package com.haizhi.graph.engine.flow.action;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;

/**
 * Created by chengmo on 2018/3/27.
 */
public class SparkSQLFunctionsMain {

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
                //.config("spark.driver.host", "10.10.10.67")
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

        /** date */
        // 1491062400
        nameDf = spark.sql("select unix_timestamp(date_sub(current_date(), 365))");
        nameDf.show();
        // 1522598400
        nameDf = spark.sql("select unix_timestamp(current_date())");
        nameDf.show();
        // 1522512000   1day=86400s, 1491062400 1year=86400*365=31536000s
        nameDf = spark.sql("select unix_timestamp(date_sub(current_date(), 365))");
        nameDf.show();

        // 2018-04-02 -> 2017-04-02
        nameDf = spark.sql("select date_sub(current_date(), 365)");
        nameDf.show();
        // 2018-04-02
        nameDf = spark.sql("select current_date()");
        nameDf.show();
        // 2018-04-02 12:01:10
        nameDf = spark.sql("select current_timestamp()");
        nameDf.show();
        // 1522639150
        nameDf = spark.sql("select unix_timestamp()");
        nameDf.show();
        // 1462464000   默认为yyyy-MM-dd HH:mm:ss，如果传入UTC时间返回null
        nameDf = spark.sql("select unix_timestamp('2016-05-06 00:00:00')");
        nameDf.show();
        // 1462464000   默认为yyyy-MM-dd HH:mm:ss，如果传入UTC时间返回null
        nameDf = spark.sql("select unix_timestamp('2016-05-06','yyyy-MM-dd')");
        nameDf.show();
        // 2018-04-02 11:19:10
        nameDf = spark.sql("select from_unixtime(unix_timestamp(), 'yyyy-MM-dd HH:mm:ss')");
        nameDf.show();
        // 2018-04-02 11:19:10
        nameDf = spark.sql("select from_utc_timestamp(from_unixtime(unix_timestamp(), 'yyyy-MM-dd HH:mm:ss'), " +
                "'Asia/Shanghai')");
        nameDf.show();
    }
}
