package com.haizhi.graph.common.redis.key;

/**
 * Created by chengmo on 2019/4/17.
 */
public class RKeys {
    // dc
    public static final String DC_DOMIAN = "com:haizhi:graph:dc:domain";
    public static final String DC_GRAPH = "com:haizhi:graph:dc:dc_graph";
    public static final String DC_SCHEMA = "com:haizhi:graph:dc:dc_schema";
    public static final String DC_ENV = "com:haizhi:graph:dc:dc_env";
    public static final String DC_ENV_FILE = "com:haizhi:graph:dc:dc_env_file";
    public static final String DC_TASK_GRAPH_ETL_GP = "com:haizhi:graph:dc:task:graph_etl_gp";
    public static final String DC_TASK_GRAPH_ERROR_MODE = "com:haizhi:graph:dc:error:mode";
    public static final String DC_ERROR_COUNT_KEY = "com:haizhi:graph:dc:error:count";

    //login
    public static final String SSO_SESSION = "haizhi:graph:";
}
