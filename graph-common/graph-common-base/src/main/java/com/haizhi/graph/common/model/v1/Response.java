package com.haizhi.graph.common.model.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.haizhi.graph.common.constant.Status;
import com.haizhi.graph.common.exception.BaseException;
import lombok.Data;
import lombok.experimental.Accessors;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/5/2.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    protected boolean success = true;
    protected Object message;
    protected Object payload;

    public Response() {
    }

    public Response(Object data) {
        this.setData(data);
    }

    public static Response success() {
        return new Response();
    }

    public static Response success(Object data) {
        return new Response(data);
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

    public static Response error(Status status) {
        Response response = new Response();
        response.setSuccess(false);
        response.setMessage(buildMessage(status));
        return response;
    }

    public static Response error(Status status, Object... args) {
        Response response = new Response();
        response.setSuccess(false);
        response.setMessage(buildMessage(status, args));
        return response;
    }

    protected static Map<String, Object> buildMessage(BaseException ex) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("code", ex.getCode());
        map.put("desc", ex.getDesc());
        return map;
    }

    protected static Map<String, Object> buildMessage(Status status) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("code", status.getCode());
        map.put("desc", status.getDesc());
        return map;
    }

    protected static Map<String, Object> buildMessage(Status status, Object... args) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("code", status.getCode());
        map.put("desc", format(status.getDesc(), args));
        return map;
    }

    private static String format(String pattern, Object... arguments) {
        return MessageFormat.format(pattern, arguments);
    }

    public Response setErrorInfo(Status status) {
        this.setMessage(status);
        return this;
    }

    public Response setData(Object data) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("data", data);
        this.setPayload(map);
        return this;
    }
}
