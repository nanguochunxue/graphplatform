package com.haizhi.graph.plugins.flume.sink;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import lombok.extern.slf4j.Slf4j;
import org.apache.flume.*;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.channel.ReplicatingChannelSelector;
import org.apache.flume.channel.file.FileChannel;
import org.apache.flume.conf.Configurables;
import org.apache.flume.event.EventBuilder;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.haizhi.graph.plugins.flume.constant.InboundFlumeConstants.HEADER_GRAPH;
import static com.haizhi.graph.plugins.flume.constant.InboundFlumeConstants.HEADER_SCHEMA;

/**
 * Created by chengangxiong on 2018/12/26
 */
@Slf4j
public class BatchGapSinkTest {

    @Test
    public void process() throws EventDeliveryException {

//        Channel channel = new MemoryChannel();

        Channel fileChannel = new FileChannel();
        fileChannel.setName("f_channel");

        Sink sink = new BatchGapSink(){
            @Override
            protected void sendRequest(DcInboundDataSuo dcInboundDataCuo) {
                if (dcInboundDataCuo.getGraph().equals("a2") && dcInboundDataCuo.getSchema().equals("b")){
                    assert dcInboundDataCuo.getRows().size() == 2;
                }
                if (dcInboundDataCuo.getGraph().equals("a1")){
                    assert dcInboundDataCuo.getRows().size() == 1;
                }
            }
        };

//        agent.channels.usualChannel.type = file
//        agent.channels.usualChannel.capacity = 100000
//        agent.channels.usualChannel.checkpointDir = /tmp/.flume/data/c1/checkpoint
//        agent.channels.usualChannel.dataDirs = /tmp/.flume/data/c1/data
        Context context = new Context();
        context.put("sink.endpointUrl", "http://localhost:9000");
        context.put("sink.batchSize", "5");
        context.put("capacity", "50000000");
        context.put("channel.checkpointDir", "/Users/haizhi/flume_data/flume_test/c1/checkpoint");
        context.put("channel.dataDirs", "/Users/haizhi/flume_data/flume_test/c1/data");

        Configurables.configure(fileChannel, context);
//        Configurables.configure(channel, context);
        Configurables.configure(sink, context);

//        channel.start();
        fileChannel.start();
        sink.setChannel(fileChannel);

        ChannelSelector channelSelector = new ReplicatingChannelSelector();
        channelSelector.setChannels(Lists.newArrayList(fileChannel));
        ChannelProcessor channelProcessor = new ChannelProcessor(channelSelector);

        for (int i = 0; i < 1000; i ++){
//        for (int i = 0; i < 20_000_000; i ++){
            channelProcessor.processEventBatch(genEventList());
        }

//        SinkProcessor sinkProcessor = new DefaultSinkProcessor();
//        sinkProcessor.setSinks(Lists.newArrayList(sink));
//        sinkProcessor.start();
//        sinkProcessor.process();

    }

    public static final String STR = "a";
    public static final byte[] STR_BYTES = "a".getBytes();

    private List<Event> genEventList() {
        List<Event> events = Lists.newArrayList();

        events.add(EventBuilder.withBody(STR_BYTES));
        events.add(EventBuilder.withBody(STR_BYTES));
        events.add(EventBuilder.withBody(STR_BYTES));
        events.add(EventBuilder.withBody(STR_BYTES));
        events.add(EventBuilder.withBody(STR_BYTES));
        events.add(EventBuilder.withBody(STR_BYTES));
        events.add(EventBuilder.withBody(STR_BYTES));
        events.add(EventBuilder.withBody(STR_BYTES));
        events.add(EventBuilder.withBody(STR_BYTES));
        events.add(EventBuilder.withBody(STR_BYTES));

        return events;
    }

    private Event buildEvent(String graph, String schema, String data) {
        Map<String, String> header = Maps.newHashMap();
        header.put(HEADER_GRAPH, graph);
        header.put(HEADER_SCHEMA, schema);
        return EventBuilder.withBody(data.getBytes(), header);
    }

}