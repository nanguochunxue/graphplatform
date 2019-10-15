package com.haizhi.graph.server.tiger.util;

import com.haizhi.graph.common.json.JSONUtils;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by tanghaiyang on 2019/3/29.
 */
public class EncodeTest {

    /*
    * throw exception
    * */
    @Test
    public void UriComponentsBuildertest() {
        String url = "http://192.168.1.101:9000/graph/query/expand";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name","ddddd");
        parameters.put("value", "sdfff eeee");

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            builder.queryParam(entry.getKey(), Objects.toString(entry.getValue(), ""));
        }
        URI uri = builder.build(true).toUri();
        String urlstr= uri.toString();
        urlstr.replace("+", "%20");
        System.out.println(uri.toString());
    }

    /*
    * URLEncode and resttem
    * */
    @Test
    public void URIBuildertest()throws Exception{
        String url = "http://192.168.1.101:9000/graph/query/expand";
        URIBuilder builder = new URIBuilder(url);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name","ddddd");
        parameters.put("value", "sdfff eeee");

        Map<String, Object> params = getParameters(parameters);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.addParameter(entry.getKey(), Objects.toString(entry.getValue(), ""));
        }
        URI uri = builder.build();

        System.out.println(uri.toString());
    }

    private <IN> Map<String, Object> getParameters(IN requestQo) {
        Map<String, Object> params;
        if (requestQo instanceof Map) {
            params = new HashMap<>((Map) requestQo);
        } else {
            params = JSONUtils.toMap(requestQo);
        }
        return params;
    }

}
