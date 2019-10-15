package com.haizhi.graph.sys.auth.constant;

/**
 * Create by zhoumingbing on 2019-06-04
 */
public enum UserType {
    COMMON(0, "普通用户"),
    PROGRAM_MANAGER(1, "项目管理员"),
    DATA_MANAGER(2, "数据管理员"),
    SYS_MANAGER(3, "系统管理员"),
    USER_DEFINED(4, "用户自定义")
    ;

    private int value;
    private String desc;

    UserType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
