package com.haizhi.graph.tag.analytics.task;

import com.google.common.collect.Maps;
import com.haizhi.graph.common.context.Resource;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.PropertiesUtils;
import com.haizhi.graph.engine.flow.action.ActionLauncher;
import com.haizhi.graph.engine.flow.action.spark.SparkActionLauncher;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.engine.flow.tools.hdfs.HDFSHelper;
import com.haizhi.graph.tag.analytics.bean.TagContext;
import com.haizhi.graph.tag.analytics.engine.driver.SimpleDriver;
import com.haizhi.graph.tag.analytics.task.context.TaskContext;
import com.haizhi.graph.tag.analytics.util.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * Created by chengmo on 2018/7/28.
 */
public class SimpleTaskExecutor extends TaskExecutor{

    private static final GLog LOG = LogFactory.getLogger(SimpleTaskExecutor.class);
    private static final String NAME = "TAG.SIMPLE";

    public void execute(TaskContext taskContext){
        if (isRunning){
            LOG.warn("Simple task is already running.");
            return;
        }
        this.isRunning = true;
        try {
            TagContext ctx = this.getTagContext(taskContext);
            ActionLauncher launcher = new SparkActionLauncher(ctx.getProperties());
            boolean success = launcher.waitForCompletion();
            LOG.info("Simple task execution result: success={0}", success);
        } catch (Exception e) {
            LOG.error(e);
        } finally {
            this.isRunning = false;
        }
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private TagContext getTagContext(TaskContext taskContext) {
        Set<Long> tagIds = taskContext.getTagIds();
        // TagContext
        TagContext ctx = new TagContext();
        String activeProfile = Resource.getActiveProfile();
        Map<String, String> conf = (Map) PropertiesUtils.load("/" + activeProfile);
        conf = Maps.filterKeys(conf, key -> { return key.startsWith(FKeys.NAME);});
        for (Map.Entry<String, String> entry : conf.entrySet()) {
            String key = StringUtils.substringAfter(entry.getKey(), FKeys.NAME);
            ctx.putProperty(key, entry.getValue());
        }
        String resourcePath = Resource.getPath();
        String jarPath = Resource.getJarPath();
        ctx.putProperty(FKeys.HADOOP_CONF_DIR, resourcePath);
        ctx.putProperty(FKeys.APP_RESOURCE, jarPath + Constants.JAR_NAME);

        HDFSHelper helper = new HDFSHelper();
        if (!helper.isEmptyDir(Constants.SPARK_YARN_JARS)){
            String sparkYarnJars = HDFSHelper.getFsURI() + Constants.SPARK_YARN_JARS + "*";
            ctx.putProperty(FKeys.SPARK_YARN_JARS, sparkYarnJars);
        }
        helper.close();
        ctx.putProperty(FKeys.MAIN_CLASS, SimpleDriver.class.getName());
        ctx.putProperty(FKeys.APP_NAME, NAME);
        ctx.putProperty(FKeys.APP_ARGS, NAME);
        return ctx;
    }
}
