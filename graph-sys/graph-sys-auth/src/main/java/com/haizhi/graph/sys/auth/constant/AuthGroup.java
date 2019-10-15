package com.haizhi.graph.sys.auth.constant;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liulu on 2019/6/19.
 */
public enum AuthGroup {

    S("S","系统级权限"),
    P("P","项目级权限"),
    SERVICE("SERVICE","服务右键菜单权限"),
    ;

    private static final Map<String, AuthGroup> codeLookup = new ConcurrentHashMap<>(4);

    static {
        for (AuthGroup authGroup : EnumSet.allOf(AuthGroup.class)){
            codeLookup.put(authGroup.code, authGroup);
        }
    }

    private String code;
    private String desc;

    AuthGroup(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public static AuthGroup fromCode(String code) {
        return codeLookup.get(code);
    }
}
