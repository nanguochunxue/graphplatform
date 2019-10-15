package com.haizhi.graph.plugins.flume.interceptor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.haizhi.graph.plugins.flume.constant.InboundFlumeConstants;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.JSONEvent;
import org.apache.flume.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by chengangxiong on 2018/12/14
 */
public class DataFormatInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(DataFormatInterceptor.class);

    @Override
    public void initialize() {
    }

    @Override
    public Event intercept(Event event) {
        JSONEvent jsonEvent = new JSONEvent();
        byte[] body = event.getBody();
        Map<String, String> headers = event.getHeaders();
        boolean csv = isCsv(headers);
        if (csv) {
            jsonEvent = assembly(body, headers);
        } else {
            String bodyStr = null;
            try {
                bodyStr = new String(body, Charset.forName("UTF-8"));
                JSON.parseObject(bodyStr, Map.class);
            } catch (Throwable th) {
                logger.warn("json data cannot formated : " + bodyStr);
                headers.put(InboundFlumeConstants.CH_MARKER, InboundFlumeConstants.UNUSUAL);
                headers.put(InboundFlumeConstants.EX_DESC, "json format error");
            }
            jsonEvent.setBody(body);
            jsonEvent.setHeaders(event.getHeaders());
        }
        return jsonEvent;
    }

    private JSONEvent assembly(byte[] body, Map<String, String> headers) {
        String[] dataArray = new String(body).split(",");
        String[] headerArray = headers.get(InboundFlumeConstants.CSV_HEADER).split(",");
        if (dataArray.length != headerArray.length) {
            headers.put(InboundFlumeConstants.CH_MARKER, InboundFlumeConstants.UNUSUAL);
            headers.put(InboundFlumeConstants.EX_DESC, "data length(" + dataArray.length + ") not equal header length(" + headerArray.length + ")");
            return newJsonEvent(headers, body);
        }
        Map<String, String> mapResult = Maps.newHashMap();
        int dataLength = dataArray.length;
        for (int i = 0; i < dataLength; i++) {
            mapResult.put(headerArray[i], dataArray[i]);
        }
        if (mapResult.isEmpty()) {
            return null;
        }
        JSONEvent jsonEvent = newJsonEvent(headers, JSON.toJSONString(mapResult).getBytes());
        return jsonEvent;
    }

    private JSONEvent newJsonEvent(Map<String, String> headers, byte[] body) {
        JSONEvent event = new JSONEvent();
        event.setHeaders(headers);
        event.setBody(body);
        return event;
    }

    private boolean isCsv(Map<String, String> headers) {
        return Boolean.valueOf(headers.get(InboundFlumeConstants.IS_CSV));
    }

    @Override
    public List<Event> intercept(List<Event> events) {

        List<Event> result = Lists.newArrayList();
        for (Event event : events) {
            Event tmpEvent = intercept(event);
            if (tmpEvent != null) {
                result.add(tmpEvent);
            }
        }
        return result;
    }

    @Override
    public void close() {

    }

    public static class Builder implements Interceptor.Builder {
        @Override
        public Interceptor build() {
            return new DataFormatInterceptor();
        }

        @Override
        public void configure(Context context) {

        }
    }
}
