package com.haizhi.graph.dc.core.constant;

/**
 * Created by chengmo on 2018/3/16.
 */
public class Constants {

    /* engine */
    public static final String JAR_NAME = "graph-dc-extract-driver.jar";

    /* HDFS path */
    //public static final String HDFS_PATH = SpringContext.getProperty("tag.analytics.hdfs.path", "/user/graph");
    public static final String HDFS_PATH = "/user/graph";
    public static final String SPARK_YARN_JARS = HDFS_PATH + "/lib/spark2x.jars/";

    public static final int BATCH_SIZE = 100;
    public static final int CONCURRENCY = 8;

    public static final String SERVER_PATH = "serverPath";
    public static final String SYS_CONFIG_API_URL = "graph-dc-inbound-api-url";
}
