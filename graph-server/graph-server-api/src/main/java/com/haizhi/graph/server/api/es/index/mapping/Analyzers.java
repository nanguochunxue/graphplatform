package com.haizhi.graph.server.api.es.index.mapping;

/**
 * Created by chengmo on 2017/12/28.
 */
public enum Analyzers {
    IK("ik"),
    SMART_CN("smartcn");

    private String code;

    Analyzers(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
