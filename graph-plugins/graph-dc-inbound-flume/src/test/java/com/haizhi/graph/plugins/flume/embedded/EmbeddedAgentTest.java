package com.haizhi.graph.plugins.flume.embedded;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.haizhi.graph.plugins.flume.source.file.core.Settings;
import com.haizhi.graph.plugins.flume.source.file.FileReadSource;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.event.EventBuilder;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by chengangxiong on 2018/12/22
 */
public class EmbeddedAgentTest {

    @Test
    public void start() throws EventDeliveryException, InterruptedException, ExecutionException {

        Map<String, String> properties = Maps.newHashMap();
        properties.put("channel.type", "memory");
        properties.put("channel.capacity", "200");
        properties.put("sinks", "sink1");
        properties.put("sink1.type", "com.haizhi.graph.plugins.flume.sink.SimpleGapSink");
        properties.put("sink1.sink.endpointUrl", "http://localhost:10010/dc/inbound/api/bulk");
        properties.put("sink1.sink.batchSize", "2000");
        properties.put("processor.type", "default");

        EmbeddedAgent agent = new EmbeddedAgent("test-aaa");
        agent.configure(properties);

        agent.start();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("graph", "test112");
        jsonObject.put("filePath", "/Users/haizhi/IdeaProjects/graph/graph-plugins/graph-dc-inbound-file/src/test/resources/data1/");
        String jsonStr = jsonObject.toJSONString();
        agent.stop();
    }

    @Test
    public void testEventSort() throws InterruptedException, ExecutionException, EventDeliveryException {
        Map<String, String> properties = Maps.newHashMap();
        properties.put("channel.type", "memory");
        properties.put("channel.capacity", "200");
        properties.put("sinks", "sink1");
        properties.put("sink1.type", "com.haizhi.graph.plugins.flume.embedded.LoggerSink");
        properties.put("processor.type", "default");

        EmbeddedAgent agent = new EmbeddedAgent("test-aaa");
        agent.configure(properties);

        agent.start();

        agent.put(EventBuilder.withBody("a".getBytes()));
        agent.put(EventBuilder.withBody("b".getBytes()));
        agent.put(EventBuilder.withBody("c".getBytes()));
        agent.put(EventBuilder.withBody("d".getBytes()));


        Thread.sleep(10 * 1000L);
        agent.stop();
    }
}