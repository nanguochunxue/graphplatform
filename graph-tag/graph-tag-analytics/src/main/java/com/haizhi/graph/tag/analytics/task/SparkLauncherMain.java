package com.haizhi.graph.tag.analytics.task;

import com.haizhi.graph.common.constant.Constants;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.ClassUtils;
import com.haizhi.graph.common.util.PropertiesUtils;
import com.haizhi.graph.engine.flow.action.spark.SparkActionLauncher;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.tag.analytics.engine.driver.SimpleDriver;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by chengmo on 2018/9/28.
 */
public class SparkLauncherMain {
    private static final GLog LOG = LogFactory.getLogger(SparkLauncherMain.class);
    private static final String BDP = "/conf";
    public static final String BDP_USER_PRINCIPAL = "bdp.user.principal";
    public static final String BDP_USER_CONF_PATH = "bdp.user.conf.path";

    public static void main(String[] args) {
        System.out.println("starting...");
        String jarPath = ClassUtils.jarForClass(SparkLauncherMain.class);
        String confPath = StringUtils.substringBeforeLast(jarPath, "/") + "/conf";
        System.out.println("confPath=" + confPath);
        Properties props = PropertiesUtils.loadFile(confPath + "/application-haizhi-fic80.properties");
        Map<String, String> conf = new HashMap<>();
        conf.put(FKeys.APP_RESOURCE, jarPath);
        conf.put(FKeys.MAIN_CLASS, SimpleDriver.class.getName());
        conf.put(FKeys.APP_NAME, "Spark-on-hive");
        conf.put(FKeys.HADOOP_CONF_DIR, confPath);
        conf.put(FKeys.SPARK_HOME, props.getProperty(FKeys.NAME + FKeys.SPARK_HOME));
        conf.put(FKeys.SPARK_DRIVER_MEMORY, props.getProperty(FKeys.NAME + FKeys.SPARK_DRIVER_MEMORY));
        conf.put(FKeys.SPARK_EXECUTOR_CORES, props.getProperty(FKeys.NAME + FKeys.SPARK_EXECUTOR_CORES));
        conf.put(FKeys.SPARK_EXECUTOR_MEMORY, props.getProperty(FKeys.NAME + FKeys.SPARK_EXECUTOR_MEMORY));
        conf.put(FKeys.SPARK_CORES_MAX, props.getProperty(FKeys.NAME + FKeys.SPARK_CORES_MAX));
        conf.put(FKeys.SPARK_YARN_JARS, props.getProperty(FKeys.NAME + FKeys.SPARK_YARN_JARS));
        String userPrincipal = props.getProperty(Constants.HADOOP_SECURITY_USERPRINCIPAL);
        String appArgs = userPrincipal + FKeys.SEPARATOR_001 + confPath;
        conf.put(FKeys.APP_ARGS, appArgs);
        System.out.println("appArgs=" + appArgs);

        conf.put(FKeys.BDP_VERSION, props.getProperty(FKeys.NAME + FKeys.BDP_VERSION));

        //System.setProperty("SPARK_HOME", "/Users/haizhi/MyFile/work/fic80/spark2x");
        System.setProperty("user.name", userPrincipal);
        SparkActionLauncher launcher = new SparkActionLauncher(conf);
        launcher.waitForCompletion();
    }
}
