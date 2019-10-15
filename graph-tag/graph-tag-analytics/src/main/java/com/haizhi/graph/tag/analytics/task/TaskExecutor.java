package com.haizhi.graph.tag.analytics.task;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.haizhi.graph.common.context.Resource;
import com.haizhi.graph.common.context.SpringContext;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.DateUtils;
import com.haizhi.graph.common.util.PropertiesUtils;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.service.DcMetadataService;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.engine.flow.tools.hdfs.HDFSHelper;
import com.haizhi.graph.tag.analytics.bean.TagContext;
import com.haizhi.graph.tag.analytics.task.context.DomainFactory;
import com.haizhi.graph.tag.analytics.task.context.TagHadoopContext;
import com.haizhi.graph.tag.analytics.task.context.TaskContext;
import com.haizhi.graph.tag.analytics.util.Constants;
import com.haizhi.graph.tag.core.bean.TagDomain;
import com.haizhi.graph.tag.core.service.TagMetadataService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/3/13.
 */
public abstract class TaskExecutor {

    private static final GLog LOG = LogFactory.getLogger(TaskExecutor.class);

    protected TagMetadataService tagService;
    protected DcMetadataService dcMetadataService;
    protected boolean isRunning;
    protected boolean debug;
    protected boolean debugLauncher;

    public TaskExecutor(){
        try {
            this.tagService = SpringContext.getBean(TagMetadataService.class);
            this.dcMetadataService = SpringContext.getBean(DcMetadataService.class);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setDebugLauncher(boolean debugLauncher) {
        this.debugLauncher = debugLauncher;
    }

    public abstract void execute(TaskContext taskContext);

    ///////////////////////
    // protected functions
    ///////////////////////
    public TagContext createTagContext(TaskContext taskContext) {
        String graphName = taskContext.getGraph();
        TagDomain tagDomain = tagService.getTagDomain(graphName);
        //Domain domain = metadataService.getDomain(graphName);
        Domain domain = DomainFactory.createOnHive(graphName);
        if (domain.invalid()){
            throw new IllegalArgumentException("invalid graph name[" + graphName + "]");
        }

        TagContext ctx = new TagContext(tagDomain, domain);
        String activeProfile = Resource.getActiveProfile();
        ctx.setKafkaTopic(SpringContext.getProperty(Constants.KAFKA_TOPIC_KEY));
        ctx.setLocationConfig(activeProfile);
        ctx.setDebugEnabled(this.debug);

        Map<String, String> conf = (Map)PropertiesUtils.load("/" + activeProfile);

        // security
        String securityEnabledStr = conf.get(com.haizhi.graph.common.constant.Constants.HADOOP_SECURITY_ENABLED);
        String userPrincipal = conf.get(com.haizhi.graph.common.constant.Constants.HADOOP_SECURITY_USERPRINCIPAL);
        ctx.setSecurityEnabled(BooleanUtils.toBoolean(securityEnabledStr));
        ctx.setUserPrincipal(userPrincipal);
        ctx.setUserConfPath(Resource.getPath());
        ctx.setBdpVersion(conf.get(FKeys.NAME + FKeys.BDP_VERSION));

        // properties
        conf = Maps.filterKeys(conf, key -> { return key.startsWith(FKeys.NAME);});
        for (Map.Entry<String, String> entry : conf.entrySet()) {
            String key = StringUtils.substringAfter(entry.getKey(), FKeys.NAME);
            ctx.putProperty(key, entry.getValue());
        }
        String resourcePath = Resource.getPath();
        String jarPath = Resource.getJarPath();
        ctx.putProperty(FKeys.HADOOP_CONF_DIR, resourcePath);
        ctx.putProperty(FKeys.APP_RESOURCE, jarPath + Constants.JAR_NAME);
        ctx.putProperty(FKeys.DEBUG_LAUNCHER, String.valueOf(debugLauncher));

        // security
        ctx.putProperty(FKeys.BDP_SECURITY_ENABLED, securityEnabledStr);
        ctx.putProperty(FKeys.SPARK_YARN_PRINCIPAL, userPrincipal);
        ctx.putProperty(FKeys.SPARK_YARN_KEYTAB, resourcePath + "/user.keytab");

        // spark.yarn.jars
        HDFSHelper helper = new HDFSHelper();
        if (!helper.isEmptyDir(Constants.SPARK_YARN_JARS)){
            String sparkYarnJars = HDFSHelper.getFsURI() + Constants.SPARK_YARN_JARS + "*";
            ctx.putProperty(FKeys.SPARK_YARN_JARS, sparkYarnJars);
        }
        helper.close();
        this.initializeHadoopContext(ctx);
        LOG.info("Context properties:\n{0}", JSON.toJSONString(ctx.getProperties(), true));
        return ctx;
    }

    private void initializeHadoopContext(TagContext ctx){
        LOG.info("Initialize hadoop context...");
        TagHadoopContext.initialize(ctx);
    }

    protected Map<String, List<String>> getDefaultPartitions(){
        String date = DateUtils.getYesterday();
        Map<String, List<String>> partitions = new LinkedHashMap<>();
        partitions.put(Constants.PARTITION_DAY, Lists.newArrayList(date));
        return partitions;
    }
}
