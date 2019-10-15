package com.haizhi.graph.dc.core.constant;

/**
 * Created by chengangxiong on 2019/04/25
 */
public enum TaskErrorType {

    CHECK_ERROR("数据校验失败"),
    RUNTIME_ERROR("更新失败"),
    ;
    private String desc;
    TaskErrorType(String desc){
        this.desc = desc;
    }
}
