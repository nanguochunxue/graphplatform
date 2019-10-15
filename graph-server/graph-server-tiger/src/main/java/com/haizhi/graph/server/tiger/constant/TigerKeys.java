package com.haizhi.graph.server.tiger.constant;

/**
 * Created by chengmo on 2019/3/13.
 */
public class TigerKeys {

    // tiger query name
    public static final String K_EXPAND_QUERY_NAME = "graphKExpand";
    public static final String SHORTEST_PATH_QUERY_NAME = "graphShortestPath";
    public static final String FULL_PATH_QUERY_NAME= "graphFullPath";

    // data importer build
    public static final String IMPORTER_KEY_VERTICES = "vertices";
    public static final String IMPORTER_KEY_EDGES = "edges";

    // CacheBuilder key
    public static final String SCHEMA_CACHE_NAME = "schema";

    // schema change job name
    public static final String SCHEMA_CREATE_JOB_NAME = "addTableJob_";

    public static final String SCHEMA_FIELD_TYPE_LONG = "Int";
    public static final String SCHEMA_FIELD_TYPE_DOUBLE = "Double";
    public static final String SCHEMA_FIELD_TYPE_DATETIME = "DateTime";
    public static final String SCHEMA_FIELD_TYPE_STRING = "String";

    // the response which build from graph sql: ls
    public static final String RESPONSE_JSON_ERROR = "error";
    public static final String RESPONSE_JSON_EXCEPTION = "exception";
    public static final String RESPONSE_JSON_RESULT = "result";
    public static final String RESPONSE_JSON_VERSION = "version";
    public static final String RESPONSE_JSON_COOKIE = "cookie";
    public static final String RESPONSE_JSON_CODE = "code";
    public static final String RESPONSE_JSON_VERTICES = "vertices";
    public static final String RESPONSE_JSON_EDGES = "edges";
    public static final String RESPONSE_JSON_GRAPHS = "graphs";
    public static final String RESPONSE_JSON_QUERIES = "queries";
    public static final String RESPONSE_JSON_JOBS = "jobs";

    // split separator for parse the response of graph sql(ls)
    public static final String RESPONSE_JSON_SPLIT_1 = "\\n";
    public static final String RESPONSE_JSON_SPLIT_2 = "-";
    public static final String RESPONSE_JSON_SPLIT_3 = "\\(";
    public static final String RESPONSE_JSON_SPLIT_4 = " ";
    public static final String RESPONSE_JSON_SPLIT_5 = ":";
    public static final String RESPONSE_JSON_SPLIT_6 = ",";

    public static final String PARAMETER_URL = "rawUrl";
    public static final String PARAMETER_HEADERS = "headers";
    public static final String PARAMETER_CHARSET = "UTF-8";
    public static final String PARAMETER_QUERY = "gdbQuery";

    public static final String PARAMETER_HEADER_AUTH = "Authorization";
    public static final String PARAMETER_HEADER_BASIC = "Basic ";
    public static final String PARAMETER_HEADER_SESSION = "session";
    public static final String PARAMETER_HEADER_EMPTY = "";
    public static final String PARAMETER_HEADER_COOKIE = "Cookie";
    public static final String PARAMETER_HEADER_SPLIT =  "#";

    public static final String RESPONSE_LINE_ENCOUNTERED = "Encountered";
    public static final String RESPONSE_LINE_NOT_EXIST= "not exist";
    public static final String RESPONSE_LINE_API_VERSION = "JSON API version";
    public static final String RESPONSE_LINE_G_COOKIE = "__GSQL__COOKIES__";
    public static final String RESPONSE_LINE_G_RETURN_CODE = "__GSQL__RETURN__CODE__";
    public static final String RESPONSE_LINE_G_Graph = "---- Graph";
    public static final String RESPONSE_LINE_G_GLOBAL = "---- Global";
    public static final String RESPONSE_LINE_VERTEX_TYPES = "Vertex Types:";
    public static final String RESPONSE_LINE_EDGE_EDGES = "Edge Types:";
    public static final String RESPONSE_LINE_GRAPHS = "Graphs:";
    public static final String RESPONSE_LINE_JOBS = "Jobs:";
    public static final String RESPONSE_LINE_QUERIES = "Queries:";

}
