package com.haizhi.graph.plugins.flume.sink;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.google.common.collect.*;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import org.apache.flume.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.haizhi.graph.plugins.flume.constant.InboundFlumeConstants.HEADER_GRAPH;
import static com.haizhi.graph.plugins.flume.constant.InboundFlumeConstants.HEADER_SCHEMA;

/**
 * Created by chengangxiong on 2018/12/14
 */
public class BatchGapSink extends SimpleGapSink {

    private Integer batchSize;

    private static final Logger logger = LoggerFactory.getLogger(BatchGapSink.class);

    @Override
    public void configure(Context context) {
        Context sinkContext = new Context(context.getSubProperties("sink."));
        batchSize = sinkContext.getInteger("batchSize", 2000);
        super.configure(context);
    }

    protected Status takeAndSendEvent(Channel channel) {
        Status status = Status.BACKOFF;
        Map<String, LinkedListMultimap<String, Map<String, Object>>> data = Maps.newHashMap();
        for (int i = 0; i < batchSize; i++) {
            Event event = channel.take();
            if (event != null) {
                String graph = event.getHeaders().get(HEADER_GRAPH);
                String schema = event.getHeaders().get(HEADER_SCHEMA);
                if (!data.containsKey(graph)) {
                    data.put(graph, LinkedListMultimap.create());
                }
                String body = new String(event.getBody(), Charsets.UTF_8);
                data.get(graph).put(schema, JSON.parseObject(body, Map.class));
                sinkCounter.incrementEventDrainAttemptCount();
                status = Status.READY;
            }else {
                status = Status.BACKOFF;
            }
        }
        for (Map.Entry<String, LinkedListMultimap<String, Map<String, Object>>> entry : data.entrySet()) {
            String graph = entry.getKey();
            LinkedListMultimap<String, Map<String, Object>> mapValue = entry.getValue();
            for (String schema : mapValue.keySet()) {
                DcInboundDataSuo preparedCuo = assemblyCuo(graph, schema, mapValue.get(schema));
                sinkCounter.addToEventDrainSuccessCount(preparedCuo.getRows().size());
                sendRequest(preparedCuo);
            }
        }
        return status;
    }
}
