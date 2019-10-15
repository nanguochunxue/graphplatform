package com.haizhi.graph.common.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.haizhi.graph.common.constant.Status;
import com.haizhi.graph.common.exception.BaseException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@ApiModel(value = "接口响应数据", description = "用于返回接口响应的内容")
public class Response<T> {

    @ApiModelProperty(value = "请求是否成功：true（成功）；false（失败）", example = "true")
    private boolean success = true;

    @ApiModelProperty(value = "请求错误信息")
    private Object message;

    @ApiModelProperty(value = "请求响应数据")
    private Payload<T> payload;

    public Response() {
        this(null);
    }

    public Response(T data) {
        this.setData(data);
    }

    public static Response success() {
        return new Response();
    }

    public static <T> Response success(T data) {
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

    public Response setData(T data) {
        if (payload == null){
            payload = new Payload(data);
        }
        this.payload.setData(data);
        return this;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(value="接口数据", description="用于表示接口数据内容")
    public static class Payload<T> {

        @ApiModelProperty(value = "数据内容")
        @JSONField(ordinal = 1)
        private T data;
    }
}
