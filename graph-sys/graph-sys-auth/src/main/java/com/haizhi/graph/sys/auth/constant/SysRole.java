package com.haizhi.graph.sys.auth.constant;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liulu on 2019/4/11.
 */
public enum SysRole {

    SUPPER_MANAGER(1L,"超级管理员"),
    SYS_MANAGER(2L,"系统管理员"),
    DATA_MANAGER(3L,"数据管理员"),
    PROJECT_MANAGER(4L,"项目管理员"),
    GENERAL_USER(5L,"普通用户"),
    ;
    
    private static final Map<Long, SysRole> codeLookup = new ConcurrentHashMap<>(5);

    static {
        for (SysRole sysRole : EnumSet.allOf(SysRole.class)){
            codeLookup.put(sysRole.code, sysRole);
        }
    }

    private Long code;
    private String name;

    SysRole(Long code, String name) {
        this.code = code;
        this.name = name;
    }

    public Long getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static SysRole fromCode(Long code) {
        return codeLookup.get(code);
    }
}
