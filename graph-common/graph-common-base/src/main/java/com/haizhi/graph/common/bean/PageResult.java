package com.haizhi.graph.common.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by chengmo on 2018/5/2.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResult extends Result {

    private long total;
    private int pageNo = 1;
    private int pageSize = 10;

    public PageResult() {
    }

    public PageResult(int pageNo, int pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public long getTotalPages(){
        return (total + pageSize - 1) / pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
