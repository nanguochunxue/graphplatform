package com.haizhi.graph.tag.analytics.util;

import com.haizhi.graph.common.context.SpringContext;

/**
 * Created by chengmo on 2018/3/16.
 */
public class Constants {

    /** Tag.suffix */
    public static final String TAG_SUFFIX = ".tag";

    /* FlowTask properties */
    public static final String TAG_DOMAIN_KEY = "tag.domain";

    /* HDFS path */
    public static final String HDFS_PATH = SpringContext.getProperty("tag.analytics.hdfs.path", "/user/");
    public static final String SPARK_YARN_JARS = HDFS_PATH + "graph/lib/spark2x.jars/";
    public static final String PATH_TAG_DOMAIN = HDFS_PATH + "graph/tag/task/TagDomain.json";
    public static final String PATH_FULL_TASK = HDFS_PATH + "graph/tag/task/full.task";
    public static final String PATH_FLOW_TASK = HDFS_PATH + "graph/tag/engine/{0}";

    /* engine */
    public static final String JAR_NAME = "graph-tag-analytics-driver.jar";
    public static final String KAFKA_TOPIC_KEY = "tag.analytics.kafka.topic";
    public static final String SCHEDULER_BATCH_MAX_SIZE = "tag.analytics.scheduler.batch.max.size";
    public static final String SCHEDULER_BATCH_TIMEOUT = "tag.analytics.scheduler.batch.timeout";
    public static final String SCHEMA_MAIN = "tag.analytics.schema.main";

    /* partition */
    public static final String PARTITION_DAY = SpringContext.getProperty("tag.analytics.partition.field", "day");

}
