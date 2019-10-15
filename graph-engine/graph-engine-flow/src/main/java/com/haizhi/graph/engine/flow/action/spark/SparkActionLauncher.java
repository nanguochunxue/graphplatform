package com.haizhi.graph.engine.flow.action.spark;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.engine.flow.action.ActionLauncher;
import com.haizhi.graph.engine.flow.action.ActionListener;
import com.haizhi.graph.engine.flow.action.YarnAppInfo;
import com.haizhi.graph.engine.flow.conf.FKeys;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by chengmo on 2018/3/14.
 */
public class SparkActionLauncher extends ActionLauncher {

    private static final GLog LOG = LogFactory.getLogger(SparkActionLauncher.class);

    private Map<String, String> conf = new HashMap<>();
    private SparkAppHandle appMonitor;
    private YarnAppInfo yarnAppInfo;
    private boolean debug;

    public SparkActionLauncher(Map<String, String> actionConfig) {
        this.argumentValidator(actionConfig);
        this.conf.putAll(actionConfig);
        this.yarnAppInfo = new YarnAppInfo();
    }

    public YarnAppInfo getYarnAppInfo() {
        return yarnAppInfo;
    }

    /**
     * Submit spark application to hadoop cluster and wait for completion.
     *
     * @return
     */
    public boolean waitForCompletion() {
        return this.waitForCompletion(null);
    }

    /**
     * Submit spark application with listener.
     *
     * @param listener
     * @return
     */
    public boolean waitForCompletion(ActionListener listener) {
        boolean success = false;
        try {
            SparkLauncher launcher = this.createSparkLauncher();
            if (debug) {
                Process process = launcher.launch();
                // Get Spark driver log
                new Thread(new ISRRunnable(process.getErrorStream())).start();
                new Thread(new ISRRunnable(process.getInputStream())).start();
                int exitCode = process.waitFor();
                System.out.println(exitCode);
                success = exitCode == 0 ? true : false;
            } else {
                appMonitor = launcher.setVerbose(true).startApplication();
                success = applicationMonitor(listener);
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return success;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private boolean applicationMonitor(ActionListener listener) {
        appMonitor.addListener(new SparkAppHandle.Listener() {
            @Override
            public void stateChanged(SparkAppHandle handle) {
                LOG.info("****************************");
                LOG.info("State Changed [state={0}]", handle.getState());
                LOG.info("AppId={0}", handle.getAppId());
                if (yarnAppInfo.getAppId() == null){
                    yarnAppInfo.setAppId(handle.getAppId());
                }
                yarnAppInfo.setAppId(handle.getState().name());
                if (listener != null){
                    listener.stateChanged(yarnAppInfo);
                }
            }

            @Override
            public void infoChanged(SparkAppHandle handle) {
            }
        });
        while (!isCompleted(appMonitor.getState())) {
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        boolean success = appMonitor.getState() == SparkAppHandle.State.FINISHED;
        return success;
    }

    private boolean isCompleted(SparkAppHandle.State state) {
        switch (state) {
            case FINISHED:
                return true;
            case FAILED:
                return true;
            case KILLED:
                return true;
            case LOST:
                return true;
        }
        return false;
    }

    private SparkLauncher createSparkLauncher() {
        //LOG.info("actionConfig:\n{0}", JSON.toJSONString(conf, true));
        this.debug = BooleanUtils.toBoolean(conf.get(FKeys.DEBUG_LAUNCHER));
        Map<String, String> env = new HashMap<>();
        String hadoopUserName = conf.get(FKeys.HADOOP_USER_NAME);
        if (StringUtils.isNotBlank(hadoopUserName)){
            env.put(FKeys.HADOOP_USER_NAME, hadoopUserName);
        }
        env.put(FKeys.HADOOP_CONF_DIR, conf.get(FKeys.HADOOP_CONF_DIR));
        env.put(FKeys.YARN_CONF_DIR, conf.get(FKeys.HADOOP_CONF_DIR));
        SparkLauncher launcher = new SparkLauncher(env);
        launcher.setAppResource(conf.get(FKeys.APP_RESOURCE));
        launcher.setMainClass(conf.get(FKeys.MAIN_CLASS));
        this.setMaster(launcher);
        // security
        /*boolean securityEnabled = BooleanUtils.toBoolean(conf.get(FKeys.BDP_SECURITY_ENABLED));
        if (securityEnabled){
            launcher.setConf(FKeys.SPARK_YARN_PRINCIPAL, conf.get(FKeys.SPARK_YARN_PRINCIPAL));
            launcher.setConf(FKeys.SPARK_YARN_KEYTAB, conf.get(FKeys.SPARK_YARN_KEYTAB));
        }*/
        launcher.setAppName(conf.get(FKeys.APP_NAME));
        launcher.setSparkHome(conf.get(FKeys.SPARK_HOME));
        launcher.setConf(SparkLauncher.DRIVER_MEMORY, conf.getOrDefault(FKeys.SPARK_DRIVER_MEMORY, "2g"));
        launcher.setConf(SparkLauncher.EXECUTOR_CORES, conf.getOrDefault(FKeys.SPARK_EXECUTOR_CORES, "2"));
        launcher.setConf(SparkLauncher.EXECUTOR_MEMORY, conf.getOrDefault(FKeys.SPARK_EXECUTOR_MEMORY, "2g"));
        launcher.setConf(FKeys.SPARK_CORES_MAX, conf.getOrDefault(FKeys.SPARK_CORES_MAX, "4"));
        String sparkYarnJars = conf.get(FKeys.SPARK_YARN_JARS);
        if (StringUtils.isNotBlank(sparkYarnJars)) {
            launcher.setConf(FKeys.SPARK_YARN_JARS, conf.get(FKeys.SPARK_YARN_JARS));
        }
        launcher.addAppArgs(new String[]{conf.get(FKeys.APP_ARGS)});
        return launcher;
    }

    private void setMaster(SparkLauncher launcher){
        boolean runLocallyEnabled = BooleanUtils.toBoolean(conf.get(FKeys.RUN_LOCALLY_ENABLED));
        if (runLocallyEnabled){
            String master = conf.get(FKeys.MASTER);
            if (StringUtils.isBlank(master)){
                master = "local[*]";
            }
            launcher.setMaster(master);
            return;
        }
        String bdpVersion = conf.getOrDefault(FKeys.BDP_VERSION, "");
        switch (bdpVersion) {
            case FKeys.BDP_VERSION_FIC80:
                launcher.setMaster("yarn-client");
                break;
            default:
                launcher.setMaster("yarn");
                launcher.setDeployMode("cluster");
        }
    }

    private void argumentValidator(Map<String, String> conf) {
        Objects.requireNonNull(conf, "conf is null");
    }
}
