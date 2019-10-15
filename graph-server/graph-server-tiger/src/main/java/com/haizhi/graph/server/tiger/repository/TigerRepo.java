package com.haizhi.graph.server.tiger.repository;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Created by tanghaiyang on 2019/3/6.
 */
public interface TigerRepo {
    
    JSONObject execute(String graphUrl);

    JSONObject execute(String graphSqlUrl, String graphSql);

    JSONObject executeDelete(String graphUrl);

    JSONObject executeUpsert(String graphUrl, Map<String, Object> data);

    // searchUrl = queryUrl + graph + queryName
    JSONObject executeQuery(String searchUrl, Map<String,String> parameters);

}
