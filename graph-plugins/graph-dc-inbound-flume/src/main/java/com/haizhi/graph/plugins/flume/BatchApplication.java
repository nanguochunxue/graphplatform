package com.haizhi.graph.plugins.flume;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.haizhi.graph.common.util.PropertiesUtils;
import com.haizhi.graph.common.util.TimeRecorder;
import com.haizhi.graph.plugins.flume.embedded.EmbeddedAgent;
import com.haizhi.graph.plugins.flume.source.file.FileReadSource;
import org.apache.commons.cli.*;
import org.apache.flume.instrumentation.util.JMXPollUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import static com.haizhi.graph.plugins.flume.constant.InboundFlumeConstants.*;

/**
 * Created by chengangxiong on 2018/12/26
 */
public class BatchApplication {

    private static final Logger logger = LoggerFactory.getLogger(BatchApplication.class);

    private static final String AGENT_NAME = "agent";
    private static final String SINK_NAME = "sink1";
    private static final String SINK_METRIC_SUCCESS_COUNT = "EventDrainSuccessCount";
    private static final String PROCESSOR_TYPE = "default";
    private static final String SINK_CLASS = "com.haizhi.graph.plugins.flume.sink.SimpleGapSink";

    public static void main(String[] args){
        Options options = new Options();
        Option option = new Option("g", "graph", true, "the name of this files data graph");
        option.setRequired(true);
        options.addOption(option);
        option = new Option("f", "config", true, "config path");
        option.setRequired(true);
        options.addOption(option);
        CommandLineParser parser = new GnuParser();
        try {
            CommandLine commandLine = parser.parse(options, args);
            String graph = commandLine.getOptionValue("g");
            String configFilePath = commandLine.getOptionValue("f");

            configAndRun(graph, configFilePath);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    static void configAndRun(String graph, String configFilePath) {
        Properties properties = PropertiesUtils.loadFile(configFilePath);
        String endpointUrl = properties.getProperty("endpointUrl");
        Preconditions.checkNotNull(endpointUrl, "endpointUrl must specify");

        Context context = new Context(properties);
        Map<String, String> agentConfig = Maps.newHashMap();
        agentConfig.put("channel.type", "memory");
        agentConfig.put("channel.capacity", context.getString(CHANNEL_SIZE, DEFAULT_CHANNEL_SIZE));
        agentConfig.put("sinks", SINK_NAME);
        agentConfig.put(SINK_NAME + ".type", SINK_CLASS);
        agentConfig.put(SINK_NAME + ".sink.endpointUrl", endpointUrl);
        agentConfig.put("processor.type", PROCESSOR_TYPE);

        EmbeddedAgent agent = new EmbeddedAgent(AGENT_NAME);
        agent.configure(agentConfig);
        FileReadSource fileReadSource = new FileReadSource(graph, agent, context);
        process(agent, fileReadSource, context.getInteger(MAX_WAIT_SECONDS, DEFAULT_MAX_WAIT_SECONDS));
    }

    static void process(EmbeddedAgent agent, FileReadSource fileReadSource, Integer maxWaitSeconds) {
        agent.start();
        FutureTask<Long> task = new FutureTask<>(fileReadSource);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(task);
        try {
            Long eventCount = task.get();
            long successCount;
            TimeRecorder recorder = new TimeRecorder(true);
            int maxWaitMillis = maxWaitSeconds * 1000;
            while ((successCount = getSuccessCount()) < eventCount &
                    (recorder.close(true).getElapsedTime() < maxWaitMillis)){
                Thread.sleep(500L);
            }
            logger.info("shutdown agent, event put [{}], event take [{}]", eventCount, successCount);
        } catch (Exception e) {
            logger.error("", e);
            agent.stop();
        }finally {
            executor.shutdownNow();
        }
        agent.stop();
    }

    private static Long getSuccessCount() {
        Map<String, Map<String, String>> metricsMap = JMXPollUtil.getAllMBeans();
        String successCountStr = metricsMap.get("SINK." + SINK_NAME).get(SINK_METRIC_SUCCESS_COUNT);
        return Long.valueOf(successCountStr);
    }

    public static class Context{
        private Properties properties;
        public Context(Properties properties){
            this.properties = properties;
        }
        public String getString(String key){
            return properties.getProperty(key);
        }
        public String getString(String key, String defaultValue){
            Object value = properties.getProperty(key, defaultValue);
            return value == null ? null : value.toString();
        }
        public Integer getInteger(String key, Integer defaultValue){
            Object value = properties.get(key);
            if (value == null){
                return defaultValue;
            }else{
                return Integer.valueOf(value.toString());
            }
        }
        public Long getLong(String key, Long defaultValue){
            Object value = properties.get(key);
            if (value == null){
                return defaultValue;
            }else{
                return Long.valueOf(value.toString());
            }
        }
    }
}
