package com.haizhi.graph.dc.inbound.task.conf;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Created by chengangxiong on 2019/03/04
 */
@Data
@NoArgsConstructor
public class LineData {

    private Map<String, String> header;

    private byte[] body;

    private LineType lineType;

    public LineData(Map<String, String> header, byte[] body, LineType lineType){
        this.header = header;
        this.body = body;
        this.lineType = lineType;
    }

    public enum LineType{
        JSON;
    }
}
