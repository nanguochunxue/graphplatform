package com.haizhi.graph.dc.inbound.engine.driver;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.haizhi.graph.common.context.Resource;
import com.haizhi.graph.dc.core.constant.Constants;
import com.haizhi.graph.dc.core.constant.TaskType;
import com.haizhi.graph.dc.inbound.engine.conf.DcFlowTask;
import com.haizhi.graph.engine.flow.action.spark.SparkActionLauncher;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.engine.flow.tools.hdfs.HDFSHelper;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengmo on 2019/2/15.
 */
public class SparkLauncherTest {

    private static final String APP_NAME_FORMAT = "{0}=>{1}.{2}";

    /**
     * 1.maven package `graph-dc-extract-driver.jar`
     * 1)mvn clean install -Dmaven.test.skip=true -Pdeploy
     * 2)mvn clean compile
     * 3)cd graph-dc
     * 4)mvn clean package -Dmaven.test.skip=true -Pdevelop -pl graph-dc-inbound -am
     */

    public static final String resourcePath =
            "/Users/haizhi/IdeaProjects/graph/graph-tag/graph-tag-analytics/src/main/resources";
    public static final String spark_home =
            "/Users/haizhi/Downloads/spark-2.1.0-bin-hadoop2.7";
    public static final String app_resource =
            "/Users/haizhi/IdeaProjects/graph/graph-api/target/" + Constants.JAR_NAME;
    public static final String Api_url =
            "http://10.10.10.7:10010/dc/inbound/api/bulk";
    @Test
    public void runLocallyOnHDFS(){
        // conf
        Map<String, String> conf = new HashMap<>();
        //String resourcePath = Resource.getPath();
        String jarPath = Resource.getJarPath();
        conf.put(FKeys.SPARK_HOME, spark_home);
        //conf.put(FKeys.HADOOP_USER_NAME, "root");
        conf.put(FKeys.HADOOP_CONF_DIR, resourcePath);
        conf.put(FKeys.APP_RESOURCE, app_resource);
        conf.put(FKeys.MAIN_CLASS, SparkExtractDriver.class.getName());
        conf.put(FKeys.DEBUG_LAUNCHER, "true");

        // task
        DcFlowTask task = new DcFlowTask();
        task.setHadoopConfDir(resourcePath);
        task.setId("1");
        task.setInboundApiUrl(Api_url);
        task.setRunLocallyEnabled(true);
        task.setDebugEnabled(true);
        task.setTaskType(TaskType.HDFS);
        //task.setSource("hdfs://hadoop01.sz.haizhi.com:8022/user/graph/dc/data/dc_inbound.json");
        task.setSource(Lists.newArrayList("/user/graph/task_d59d76d9-55b5-4c47-9894-64741604acef.json"));
//        task.setSource(Lists.newArrayList("select * from crm_dev2.to_account"));
        if (task.isRunLocallyEnabled()){
            conf.put(FKeys.RUN_LOCALLY_ENABLED, "true");
            conf.put(FKeys.MASTER, task.getMaster());
        }
        conf.put(FKeys.APP_NAME, getAppName(task));
        conf.put(FKeys.APP_ARGS, JSON.toJSONString(task));

        // spark.yarn.jars
        HDFSHelper helper = new HDFSHelper(task.getHadoopConfDir());
        if (!helper.isEmptyDir(Constants.SPARK_YARN_JARS)){
            String sparkYarnJars = HDFSHelper.getFsURI() + Constants.SPARK_YARN_JARS + "*";
            conf.put(FKeys.SPARK_YARN_JARS, sparkYarnJars);
        }
        helper.close();

        // launcher
        SparkActionLauncher launcher = new SparkActionLauncher(conf);
        launcher.waitForCompletion();
    }

    private String getAppName(DcFlowTask task) {
        String taskId = task.getId();
        return MessageFormat.format(APP_NAME_FORMAT, "DC.EXTRACT",
                task.getTaskType(), taskId);
    }
}
