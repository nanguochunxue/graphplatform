package com.haizhi.graph.plugins.flume.constant;

/**
 * Created by chengangxiong on 2018/12/25
 */
public class InboundFlumeConstants {
    /**
     * csv file header
     */
    public static final String CSV_HEADER = "csvHeader";
    /**
     * whether is a CSV file
     */
    public static final String IS_CSV = "isCsv";
    /**
     * used in conf.properties, key in header to diff channel
     */
    public static final String CH_MARKER = "chMarker";
    public static final String UNUSUAL = "unusual";
    /**
     * when data cannot format in DataFormatInterceptor, the ex desc will wrote in header
     */
    public static final String EX_DESC = "exDesc";
    /**
     * config in header used in jpaSink for data assembly
     */
    public static final String HEADER_GRAPH = "graph";
    public static final String HEADER_SCHEMA = "schema";

    public static final String CHANNEL_SIZE = "channel.size";
    public static final String DEFAULT_CHANNEL_SIZE = "2000";

    public static final String MAX_WAIT_SECONDS = "max.wait.seconds";
    public static final Integer DEFAULT_MAX_WAIT_SECONDS = 600;

    public static final String MAX_LINE_SIZE = "max.line.size";
    public static final Integer DEFAULT_MAX_LINE_SIZE = 200;
}
