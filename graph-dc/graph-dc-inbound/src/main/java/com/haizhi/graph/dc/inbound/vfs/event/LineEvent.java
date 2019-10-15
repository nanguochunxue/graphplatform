package com.haizhi.graph.dc.inbound.vfs.event;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengangxiong on 2019/01/28
 */
public class LineEvent {

    public static final String CSV_HEADER = "csv_header";

    private Map<String, Object> headers;
    private String body;

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "LineEvent{" +
                "headers=" + headers +
                ", body=" + body +
                '}';
    }

    public static final class Builder{

        public static LineEvent with(String body, Map<String, Object> headers) {
            if (body == null){
                return null;
            }
            LineEvent event = new LineEvent();
            event.setBody(body);

            if (headers != null) {
                event.setHeaders(new HashMap<>(headers));
            }
            return event;
        }
    }

}
