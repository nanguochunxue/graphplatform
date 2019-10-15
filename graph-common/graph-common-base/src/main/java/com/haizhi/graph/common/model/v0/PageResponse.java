package com.haizhi.graph.common.model.v0;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.haizhi.graph.common.bean.PageResult;
import com.haizhi.graph.common.exception.BaseException;
import lombok.Data;

/**
 * Created by chengmo on 2018/5/2.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse extends Response {

    private long total;
    private int pageNo = 1;
    private int pageSize = 10;

    public PageResponse() {
    }

    public PageResponse(int pageNo, int pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
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

    public static PageResponse get(PageResult result) {
        PageResponse response = new PageResponse();
        response.setSuccess(result.isSuccess());
        response.setMessage(result.getMessage());
        response.setPageNo(result.getPageNo());
        response.setPageSize(result.getPageSize());
        response.setTotal(result.getTotal());
        response.setPayload(result.getData());
        return response;
    }
}
