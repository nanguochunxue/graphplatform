package com.haizhi.graph.plugins.flume.sink;

import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.model.v1.Response;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.plugins.dc.inbound.api.impl.DcInboundServiceImpl;
import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.haizhi.graph.plugins.flume.constant.InboundFlumeConstants.HEADER_GRAPH;
import static com.haizhi.graph.plugins.flume.constant.InboundFlumeConstants.HEADER_SCHEMA;

/**
 * Created by chengangxiong on 2018/12/22
 */
public class SimpleGapSink extends AbstractSink implements Configurable {
    private DcInboundServiceImpl dcInboundService;
    protected SinkCounter sinkCounter;
    private String endpointUrl;

    private static final Logger logger = LoggerFactory.getLogger(SimpleGapSink.class);

    @Override
    public void configure(Context context) {
        dcInboundService = new DcInboundServiceImpl();
        if (this.sinkCounter == null) {
            this.sinkCounter = new SinkCounter(this.getName());
        }
        Context sinkContext = new Context(context.getSubProperties("sink."));
        endpointUrl = sinkContext.getString("endpointUrl");
        Preconditions.checkNotNull(endpointUrl, "endpointUrl in httpSink cannot be null");
    }

    @Override
    public synchronized void start() {
        logger.info("Starting BatchGapSink");
        sinkCounter.start();
        super.start();
    }

    @Override
    public synchronized void stop() {
        logger.info("Stopping sink");
        sinkCounter.stop();
        super.start();
    }

    @Override
    public Status process() {
        Status status;
        Channel channel = this.getChannel();
        Transaction txn = channel.getTransaction();
        txn.begin();
        try {
            return takeAndSendEvent(channel);
        } catch (Throwable th) {
            logger.error("sink data got exception", th);
        } finally {
            txn.commit();
            status = Status.READY;
            txn.close();
        }
        return status;
    }

    protected void sendRequest(DcInboundDataSuo dcInboundDataCuo) {
        try {
            Response response = dcInboundService.bulkInbound(endpointUrl, dcInboundDataCuo);
            if (response != null && !response.isSuccess()) {
                logger.warn("server response NOT success, msg: {}", response == null ? null : response.getMessage());
            }
            sinkCounter.incrementEventDrainSuccessCount();
        }catch (Exception ex){
            logger.error("send req got exception", ex);
        }
    }

    protected Status takeAndSendEvent(Channel channel) {
        DcInboundDataSuo cuo = null;
        Event event = channel.take();
        if (event != null) {
            sinkCounter.incrementEventDrainAttemptCount();
            String graph = event.getHeaders().get(HEADER_GRAPH);
            String schema = event.getHeaders().get(HEADER_SCHEMA);
            cuo = assemblyCuo(graph, schema, JSONArray.parseObject(new String(event.getBody(), Charsets.UTF_8), List.class));
            if (cuo != null) {
                sendRequest(cuo);
            }
            return Status.READY;
        }else {
            return Status.BACKOFF;
        }
    }

    protected DcInboundDataSuo assemblyCuo(String graph, String schema, List<Map<String, Object>> data) {
        DcInboundDataSuo dcInboundDataCuo = new DcInboundDataSuo();
        dcInboundDataCuo.setOperation(GOperation.CREATE_OR_UPDATE);
        dcInboundDataCuo.setRows(data);
        dcInboundDataCuo.setGraph(graph);
        dcInboundDataCuo.setSchema(schema);
        return dcInboundDataCuo;
    }
}
