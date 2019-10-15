package com.haizhi.graph.plugins.etl.gp.constant;

import com.haizhi.graph.common.constant.Status;

/**
 * Created by chengangxiong on 2019/05/09
 */
public enum GpStatus implements Status {
    PERL_SCRIPT_NOT_FOUND(4001, "找不到perl脚本");

    private int code;
    private String desc;

    GpStatus(int code, String desc) {
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
