package com.haizhi.graph.common.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by chengmo on 2018/5/2.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result {

    protected boolean success = true;
    protected Object message;
    protected Object data;

    public Result() {
    }

    public Result(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
