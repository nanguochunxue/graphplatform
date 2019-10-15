package com.haizhi.graph.common.rest;

import org.springframework.core.ParameterizedTypeReference;

import java.util.Map;

/**
 * Created by chengmo on 2018/5/16.
 */
public interface RestService {

    /**
     * Call restful service with http get method.
     *
     * @param url
     * @param request
     * @param responseType
     * @return
     * @throws Exception
     */
    <Req, Resp> Resp doGet(String url, Req request, Class<Resp> responseType) throws Exception;

    /**
     * Call restful service with http get method.
     *
     * @param url
     * @param request
     * @param reference
     * @param <Req>
     * @param <Resp>
     * @return
     * @throws Exception
     */
    <Req, Resp> Resp doGet(String url, Req request, ParameterizedTypeReference<Resp> reference) throws Exception;

    /**
     * Call restful service with http post method.
     *
     * @param url
     * @param responseType
     * @return
     * @throws Exception
     */
    <Resp> Resp doPost(String url, Class<Resp> responseType) throws Exception;

    /**
     * Call restful service with http post method.
     *
     * @param url
     * @param request
     * @param responseType
     * @return
     * @throws Exception
     */
    <Req, Resp> Resp doPost(String url, Req request, Class<Resp> responseType) throws Exception;

    /**
     * Call restful service with http post method.
     *
     * @param url
     * @param request
     * @param responseType
     * @param requestHeaders
     * @return
     * @throws Exception
     */
    <Req, Resp> Resp doPost(String url, Req request, Class<Resp> responseType, Map<String, String> requestHeaders)
            throws Exception;

    /**
     * Call restful service with http post method.
     *
     * @param url
     * @param request
     * @param reference
     * @param <Req>
     * @param <Resp>
     * @return
     * @throws Exception
     */
    <Req, Resp> Resp doPost(String url, Req request, ParameterizedTypeReference<Resp> reference) throws Exception;



    <Req, Resp> Resp doPost(String url,Req request, ParameterizedTypeReference<Resp> reference, Map<String,String> headerParams) throws Exception;

    /**
     * Call restful service with http post async method.
     *
     * @param url
     * @param request
     * @param reference
     * @return
     * @throws Exception
     */
    <Req, Resp> Resp doPostAsync(String url, Req request, ParameterizedTypeReference<Resp> reference) throws Exception;

    /**
     * Call restful service with http delete method.
     *
     * @param url
     * @param request
     * @param responseType
     * @return
     * @throws Exception
     */
    <Req, Resp> Resp doDelete(String url, Req request, Class<Resp> responseType) throws Exception;
}
