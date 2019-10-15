package com.haizhi.graph.sys.auth.constant;

/**
 * Created by liulu on 2019/6/18.
 */
public enum UserSource {

    SSO("SSO", "单点登录"),
    SYS_CREATION("SYS_CREATION", "系统创建"),
    BUILD_IN("BUILD_IN","内置"),
    ;

    private String code;
    private String desc;

    UserSource(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
