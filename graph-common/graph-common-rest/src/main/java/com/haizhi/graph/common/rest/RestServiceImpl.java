package com.haizhi.graph.common.rest;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haizhi.graph.common.json.JSONUtils;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by chengmo on 2018/5/16.
 */
@NoArgsConstructor
@Component
public class RestServiceImpl implements RestService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    public RestServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /*
     * urlencode apply to RFC1738, it encode blank to +,
     * but tiger server use RFC 2396 standard, it can not decode + to blank,
     * and restTemplate use private encode method : HierarchicalUriComponents.encodeBytes
     * so need replace blank to %20, for example : replace(" ", "%20")
     * comment by tanghaiyang
    * */
    @Override
    @SuppressWarnings("all")
    public <Req, Resp> Resp doGet(String url, Req request, Class<Resp> responseType) throws Exception {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        Map<String, Object> parameters = (Map<String, Object>) request;
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            builder.queryParam(entry.getKey(), Objects.toString(entry.getValue(), ""));
        }
        return restTemplate.getForObject(builder.build(false).toUri(), responseType);
    }

    @Override
    public <Req, Resp> Resp doGet(String url, Req request, ParameterizedTypeReference<Resp> responseType) throws
            Exception {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        Map<String, Object> parameters = (Map<String, Object>) request;
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            builder.queryParam(entry.getKey(), Objects.toString(entry.getValue(), ""));
        }
        return restTemplate.exchange(builder.build(false).toUri(), HttpMethod.GET, null, responseType).getBody();
    }

    @Override
    public <Resp> Resp doPost(String url, Class<Resp> responseType) throws Exception {
        return doPost(url, null, responseType, null);
    }

    @Override
    public <Req, Resp> Resp doPost(String url, Req request, Class<Resp> responseType) throws Exception {
        return doPost(url, request, responseType, null);
    }

    @Override
    public <Req, Resp> Resp doPost(String url, Req request, Class<Resp> responseType, Map<String, String>
            requestHeaders) throws Exception {
        // headers
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setAcceptCharset(Collections.singletonList(Charset.forName("UTF-8")));
        if (!MapUtils.isEmpty(requestHeaders)) {
            for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                httpHeaders.add(entry.getKey(), entry.getValue());
            }
        }
        // body
        String jsonBody;
        if (request instanceof String) {
            jsonBody = (String) request;
        } else {
            if (Objects.isNull(request)) {
                jsonBody = new ObjectMapper().writeValueAsString(new JSONObject());
            } else {
                jsonBody = new ObjectMapper().writeValueAsString(request);
            }
        }
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, httpHeaders);
        return restTemplate.postForObject(url, httpEntity, responseType);
    }

    @Override
    public <Req, Resp> Resp doPost(String url, Req request,ParameterizedTypeReference<Resp> respType) throws Exception {
        if (request == null) {
            return restTemplate.exchange(url, HttpMethod.POST, null, respType).getBody();
        }
        // headers
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setAcceptCharset(Collections.singletonList(Charset.forName("UTF-8")));
        // body
        String jsonBody = new ObjectMapper().writeValueAsString(request);
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, httpHeaders);
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, respType).getBody();
    }

    @Override
    public <Req, Resp> Resp doPost(String url,Req request, ParameterizedTypeReference<Resp> respType,Map<String,String> headerParams) throws Exception {
        if (request == null) {
            return restTemplate.exchange(url, HttpMethod.POST, null, respType).getBody();
        }
        // headers
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setAcceptCharset(Collections.singletonList(Charset.forName("UTF-8")));
        if (Objects.nonNull(headerParams) && (headerParams.size() > 0)){
            headerParams.forEach((param,value) -> httpHeaders.add(param,value));
        }

        // body
        String jsonBody = new ObjectMapper().writeValueAsString(request);
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, httpHeaders);
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, respType).getBody();
    }

    @Override
    public <Req, Resp> Resp doPostAsync(String url, Req request,ParameterizedTypeReference<Resp> respType) throws Exception {
        if (request == null) {
            return asyncRestTemplate.exchange(url, HttpMethod.POST, null, respType).get().getBody();
        }
        // headers
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setAcceptCharset(Collections.singletonList(Charset.forName("UTF-8")));
        // body
        String jsonBody = new ObjectMapper().writeValueAsString(request);
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, httpHeaders);
        return asyncRestTemplate.exchange(url, HttpMethod.POST, httpEntity, respType).get().getBody();
    }

    @Override
    public <Req, Resp> Resp doDelete(String url, Req request, Class<Resp> responseType) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> params = this.getParameters(request);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            headers.add(entry.getKey(), Objects.toString(entry.getValue(), ""));
        }
        ResponseEntity<Resp> exchange = restTemplate.exchange(url, HttpMethod.DELETE,
                new HttpEntity<>(headers), responseType);
        return exchange.getBody();
    }

    ///////////////////////
    // private functions
    ///////////////////////
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
