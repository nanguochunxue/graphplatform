package com.haizhi.graph.sys.core.constant;

import com.haizhi.graph.common.constant.Status;

/**
 * Created by liulu on 2019/4/3.
 */
public enum SysCoreStatus implements Status {
    /**
     * graph-sys-core     30000~39999
     */
    CALL_REST_ERROR(30000, "call restful api exception,{0}"),
    URL_INVALID_ERROR(30001, "invalid url,{0}"),
    ;
    private int code;
    private String desc;

    SysCoreStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
