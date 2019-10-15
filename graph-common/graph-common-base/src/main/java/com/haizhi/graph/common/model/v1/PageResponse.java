package com.haizhi.graph.common.model.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.haizhi.graph.common.exception.BaseException;
import com.haizhi.graph.common.model.v0.Response;
import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/5/2.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse extends Response {

    private long total;
    private int pageNo = 1;
    private int pageSize = 10;
    private Map<String, Object> body = new HashMap<>();

    public PageResponse() {
    }

    public PageResponse(long total, int pageNo, int pageSize) {
        this.setPageInfo(total, pageNo, pageSize);
    }

    public PageResponse(Collection<?> data, long total, int pageNo, int pageSize) {
        this(total, pageNo, pageSize);
        this.body.put("data", data);
    }

    public long getTotalPages(){
        return (total + pageSize - 1) / pageSize;
    }

    public static PageResponse success(){
        return new PageResponse();
    }

    public static PageResponse error(){
        PageResponse response = new PageResponse();
        response.setSuccess(false);
        return response;
    }

    public static PageResponse error(BaseException ex) {
        PageResponse response = new PageResponse();
        response.setSuccess(false);
        response.setMessage(buildMessage(ex));
        return response;
    }

    public PageResponse setPageInfo(long total, int pageNo, int pageSize) {
        this.body.put("total", total);
        this.body.put("pageNo", pageNo);
        this.body.put("pageSize", pageSize);
        return this;
    }
}
