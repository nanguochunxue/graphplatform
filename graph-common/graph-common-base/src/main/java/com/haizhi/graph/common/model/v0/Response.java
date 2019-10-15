package com.haizhi.graph.common.model.v0;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.haizhi.graph.common.bean.Result;
import com.haizhi.graph.common.exception.BaseException;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/5/2.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    protected boolean success = true;
    protected Object message;
    protected Object payload;

    public Response() {
    }

    public Response(Object payload) {
        this.payload = payload;
    }

    public static Response success() {
        return new Response();
    }

    public static Response success(Object payload) {
        return new Response(payload);
    }

    public static Response error() {
        Response response = new Response();
        response.setSuccess(false);
        return response;
    }

    public static Response error(String message) {
        Response response = new Response();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    public static Response error(BaseException ex) {
        Response response = new Response();
        response.setSuccess(false);
        response.setMessage(buildMessage(ex));
        return response;
    }

    public static Response get(Result result) {
        Response response = new Response();
        response.setSuccess(result.isSuccess());
        response.setMessage(result.getMessage());
        response.setPayload(result.getData());
        return response;
    }

    protected static Map<String, Object> buildMessage(BaseException ex){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("code", ex.getCode());
        map.put("desc", ex.getDesc());
        return map;
    }
}
