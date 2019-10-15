package com.haizhi.graph.server.api.constant;

/**
 * Created by chengmo on 2019/6/20.
 */
public class EKeys {

    public static final String key = "key";
    public static final String type = "type";
    public static final String schema = "schema";
    public static final String field = "field";
    public static final String stats = "stats";
    public static final String aggregation = "aggregation";     // old EsQuery para
    public static final String aggregations = "aggregations";   // es default use para
    public static final String ranges = "ranges";
    public static final String rangeKey = "rangeKey";

    public static final String fieldType = "fieldType";
    public static final String operator = "operator";
    public static final String boost = "boost";
    public static final String logicOperator = "logicOperator";
    public static final String rules = "rules";
    public static final String value = "value";
    public static final String from = "from";
    public static final String to = "to";
    public static final String includeLower = "includeLower";
    public static final String includeUpper = "includeUpper";
    public static final String _keyword =  ".keyword";

    // unity with the naming conventions mentioned above
    public static final String hits =  "hits";
    public static final String _source =  "_source";
    public static final String total =  "total";

    public static final float tieBreaker = 0.3F;
}
