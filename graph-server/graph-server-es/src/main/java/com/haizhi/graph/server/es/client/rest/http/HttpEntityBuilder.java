package com.haizhi.graph.server.es.client.rest.http;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.haizhi.graph.server.api.es.index.bean.Source;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/11/13.
 */
public class HttpEntityBuilder {
    private static final int MAX_ROWS_LIMIT = 5000;

    public static HttpEntity create(List<Source> sources) {
        if (sources == null || sources.isEmpty()) {
            throw new IllegalArgumentException("sources is empty");
        }
        int size = sources.size();
        if (sources.size() > MAX_ROWS_LIMIT) {
            throw new IllegalArgumentException("max rows limit 5000, current " + size);
        }
        return new NStringEntity(createBody(sources), ContentType.APPLICATION_JSON);
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private static String createBody(List<Source> sources){
        StringBuilder sb = new StringBuilder();
        for (Source source : sources) {
            Map<String, Object> index = new HashMap<>();
            index.put("index", ImmutableMap.of("_id", source.getId()));
            sb.append(JSON.toJSONString(index)).append("\n");
            sb.append(JSON.toJSONString(source.getSource())).append("\n");
        }
        //System.out.println(sb);
        return sb.toString();
    }
}
