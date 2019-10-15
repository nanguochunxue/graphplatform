package com.haizhi.graph.engine.flow.conf;

/**
 * Created by chengmo on 2018/3/14.
 */
public class FKeys {

    /* Separators */
    public static final String SEPARATOR_001 = "\001";
    public static final String SEPARATOR_002 = "\002";
    public static final String SEPARATOR_003 = "\003";

    /* BDP */
    public static final String BDP_VERSION = "bdp.version";
    public static final String BDP_VERSION_FIC80 = "fic80";
    public static final String BDP_SECURITY_ENABLED = "security.enabled";

    /* Debug */
    public static final String DEBUG_LAUNCHER = "debug.launcher";
    public static final String NAME = "engine.flow.";
    public static final String RUN_LOCALLY_ENABLED = "run_locally.enabled";

    /* Kafka */
    public static final String KAFKA_TOPIC = "kafka_topic";
    public static final String KAFKA_LOCATION_CONFIG = "kafka_location_config";

    /* Configuration */
    public static final String FS_DEFAULT = "fs.defaultFS";

    /* Action */
    public static final String MASTER = "master";
    public static final String APP_NAME = "app_name";
    public static final String HADOOP_USER_NAME = "HADOOP_USER_NAME";
    public static final String HADOOP_CONF_DIR = "HADOOP_CONF_DIR";
    public static final String YARN_CONF_DIR = "YARN_CONF_DIR";
    public static final String APP_RESOURCE = "APP_RESOURCE";
    public static final String MAIN_CLASS = "MAIN_CLASS";
    public static final String SPARK_HOME = "spark.home";
    public static final String SPARK_YARN_JARS = "spark.yarn.jars";
    public static final String SPARK_DRIVER_MEMORY = "spark.driver.memory";
    public static final String SPARK_EXECUTOR_CORES = "spark.executor.cores";
    public static final String SPARK_EXECUTOR_MEMORY = "spark.executor.memory";
    public static final String SPARK_CORES_MAX = "spark.cores.max";
    public static final String APP_ARGS = "app.args";
    public static final String SPARK_YARN_PRINCIPAL = "spark.yarn.principal";
    public static final String SPARK_YARN_KEYTAB = "spark.yarn.keytab";
}
