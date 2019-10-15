package com.haizhi.graph.plugins.flume.sink;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import org.apache.flume.*;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.channel.ReplicatingChannelSelector;
import org.apache.flume.conf.Configurables;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.sink.DefaultSinkProcessor;
import org.junit.Test;
import org.mortbay.util.ajax.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.haizhi.graph.plugins.flume.constant.InboundFlumeConstants.HEADER_GRAPH;
import static com.haizhi.graph.plugins.flume.constant.InboundFlumeConstants.HEADER_SCHEMA;

/**
 * Created by chengangxiong on 2018/12/26
 */
public class SimpleGapSinkTest {

    @Test
    public void process() throws EventDeliveryException {

        SimpleGapSink simpleGapSink = new SimpleGapSink(){
            @Override
            protected void sendRequest(DcInboundDataSuo dcInboundDataCuo) {
                assert dcInboundDataCuo.getGraph().equals("graph");
                assert dcInboundDataCuo.getSchema().equals("schema");
                assert dcInboundDataCuo.getRows().get(0).get("a").equals("a-value");
                assert dcInboundDataCuo.getRows().get(0).get("b").equals("b-value");
            }};
        Channel channel = new MemoryChannel();

        Context context = new Context();
        context.put("sink.endpointUrl", "http://localhost:9000");

        Configurables.configure(channel, context);
        Configurables.configure(simpleGapSink, context);

        simpleGapSink.setChannel(channel);
        channel.start();
        simpleGapSink.start();

        List<Channel> channels = new ArrayList<Channel>();
        channels.add(channel);

        ChannelSelector rcs = new ReplicatingChannelSelector();
        rcs.setChannels(channels);

        ChannelProcessor processor = new ChannelProcessor(rcs);

        SinkProcessor sinkProcessor = new DefaultSinkProcessor();

        sinkProcessor.setSinks(Lists.newArrayList(simpleGapSink));
        sinkProcessor.start();

        Map<String, String> headers = Maps.newHashMap();
        headers.put(HEADER_GRAPH, "graph");
        headers.put(HEADER_SCHEMA, "schema");

        Map<String, String> data = Maps.newHashMap();
        data.put("a", "a-value");
        data.put("b", "b-value");
        processor.processEvent(EventBuilder.withBody(JSON.toString(Lists.newArrayList(data)), Charsets.UTF_8, headers));

        sinkProcessor.process();

    }
}