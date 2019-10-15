package com.haizhi.graph.sys.auth.constant;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liulu on 2019/4/15.
 */
public enum SysType {

    GAP("GAP","图分析平台"),
    SYS("SYS","图平台系统模块"),
    DMP("DMP","图数据平台"),
    AP("AP","图应用平台"),
    ;

    private static final Map<String, SysType> codeLookup = new ConcurrentHashMap<>(4);

    static {
        for (SysType sysType : EnumSet.allOf(SysType.class)){
            codeLookup.put(sysType.code, sysType);
        }
    }

    private String code;
    private String desc;

    SysType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public static SysType fromCode(String code) {
        return codeLookup.get(code);
    }
}

