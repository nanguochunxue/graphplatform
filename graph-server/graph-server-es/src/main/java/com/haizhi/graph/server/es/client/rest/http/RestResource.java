package com.haizhi.graph.server.es.client.rest.http;

/**
 * Created by chengmo on 2018/11/8.
 */
public class RestResource {
    public static final String POST = "POST";
    public static final String BULK = "_bulk";

    public static String bulk(String index, String type){
        return "/" + index + "/" + type + "/" + BULK;
    }
}
