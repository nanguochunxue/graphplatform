package com.haizhi.graph.tag.analytics.engine.driver;

import com.haizhi.graph.engine.flow.action.LauncherDriver;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.server.hbase.conf.LoginUtil;
import com.haizhi.graph.tag.analytics.util.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;

/**
 * Created by chengmo on 2018/7/28.
 */
public class SimpleDriver extends LauncherDriver {

    public static void main(String[] args) throws Exception {
        run(SimpleDriver.class, args);
    }

    @Override
    protected void run(String[] args) throws Exception {
        if (args.length > 0){
            this.debug = args[0].contains("debug");
        }
        System.out.println("prepare login");
        login(args);
        SparkSession spark = this.getOrCreate(true);
        System.out.println("SparkSession startup...");

        Dataset<Row> nameDf;
        nameDf = spark.sql("SELECT COUNT(*) FROM employees_info");
        SqlUtils.println("SELECT COUNT(*) FROM employees_info");
        nameDf.show();

        nameDf = spark.sql("SELECT COUNT(*) FROM crm_dev2.to_balance");
        SqlUtils.println("SELECT COUNT(*) FROM crm_dev2.to_balance");
        nameDf.show();

        spark.sql("USE crm_dev2");
        nameDf = spark.sql("SELECT COUNT(*) FROM to_active_product");
        SqlUtils.println("SELECT COUNT(*) FROM to_active_product");
        nameDf.show();

        nameDf = spark.sql("select current_timestamp()");
        SqlUtils.println("select current_timestamp()");
        nameDf.show();

        nameDf = spark.sql("SELECT get_json_object('{\"a\":\"b\"}', '$.a')");
        SqlUtils.println("SELECT get_json_object");
        nameDf.show();

        System.out.println("prepare stop...");
        spark.stop();
        System.out.println("stop...");
    }

    @Override
    public SparkSession getOrCreate(boolean enableHiveSupport){
        SparkSession.Builder builder= SparkSession.builder()
                .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .config("spark.rdd.compress", "true");
        if (enableHiveSupport){
            builder.enableHiveSupport();
        }
        if (debug){
            builder.master("local[*]");
        }
        return builder.getOrCreate();
    }

    private void login(String[] args){
        try {
            String appArgs = args[0];
            System.out.println(appArgs);
            String userPrincipal = StringUtils.substringBefore(appArgs, FKeys.SEPARATOR_001);
            String userConf = StringUtils.substringAfter(appArgs, FKeys.SEPARATOR_001);
            System.setProperty("HADOOP_USER_NAME", userPrincipal);
            System.setProperty("user.name", userPrincipal);
            //String userPrincipal = "developuser";
            String userKeytabPath = userConf + "/user.keytab";
            String krb5ConfPath = userConf + "/krb5.conf";
            String ZKServerPrincipal = "zookeeper/hadoop.hadoop.com";

            String ZOOKEEPER_DEFAULT_LOGIN_CONTEXT_NAME = "Client";
            String ZOOKEEPER_SERVER_PRINCIPAL_KEY = "zookeeper.server.principal";

            Configuration hadoopConf = new Configuration();
            //LoginUtil.setJaasConf(ZOOKEEPER_DEFAULT_LOGIN_CONTEXT_NAME, userPrincipal, userKeytabPath);
            //LoginUtil.setZookeeperServerPrincipal(ZOOKEEPER_SERVER_PRINCIPAL_KEY, ZKServerPrincipal);
            LoginUtil.login(userPrincipal, userKeytabPath, krb5ConfPath, hadoopConf);
            System.out.println("Success to login hbase!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
