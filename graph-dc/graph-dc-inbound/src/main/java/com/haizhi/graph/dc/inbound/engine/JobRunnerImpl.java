package com.haizhi.graph.dc.inbound.engine;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.context.SpringContext;
import com.haizhi.graph.dc.core.constant.Constants;
import com.haizhi.graph.dc.inbound.engine.conf.DcFlowTask;
import com.haizhi.graph.dc.inbound.engine.driver.SparkExtractDriver;
import com.haizhi.graph.engine.flow.action.ActionListener;
import com.haizhi.graph.engine.flow.action.spark.SparkActionLauncher;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.engine.flow.tools.hdfs.HDFSHelper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengangxiong on 2019/02/14
 */
@Component
public class JobRunnerImpl implements JobRunner {

//    @Value("${graph.dc.inbound.hadoopConfigDir}")
    private String hadoopConfigDir;

//    @Value("${graph.dc.inbound.sparkLaunchJar}")
    private String sparkLaunchJar;

    @Override
    public boolean waitForCompletion(DcFlowTask task) {
        return waitForCompletion(task, null);
    }

    @Override
    public boolean waitForCompletion(DcFlowTask task, ActionListener listener) {
        Map<String, String> conf = new HashMap<>();
        String resourcePath = hadoopConfigDir;
        conf.put(FKeys.SPARK_HOME, SpringContext.getProperty(FKeys.NAME + FKeys.SPARK_HOME));
        conf.put(FKeys.HADOOP_CONF_DIR, resourcePath);
        conf.put(FKeys.APP_RESOURCE, sparkLaunchJar);
        conf.put(FKeys.MAIN_CLASS, SparkExtractDriver.class.getName());
        conf.put(FKeys.DEBUG_LAUNCHER, String.valueOf(task.isDebugLauncher()));
        if (task.isRunLocallyEnabled()){
            conf.put(FKeys.RUN_LOCALLY_ENABLED, "true");
            conf.put(FKeys.MASTER, task.getMaster());
        }
        conf.put(FKeys.APP_NAME, task.getId());
        conf.put(FKeys.APP_ARGS, JSON.toJSONString(task));
        String sparkYarnJars = HDFSHelper.getFsURI() + Constants.SPARK_YARN_JARS + "*";
        conf.put(FKeys.SPARK_YARN_JARS, sparkYarnJars);
        SparkActionLauncher launcher = new SparkActionLauncher(conf);
        return launcher.waitForCompletion(listener);
    }
}
