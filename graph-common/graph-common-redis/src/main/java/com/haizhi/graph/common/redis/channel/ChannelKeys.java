package com.haizhi.graph.common.redis.channel;

/**
 * Created by chengmo on 2019/6/24.
 */
public class ChannelKeys {
    // channel
    private static final String PREFIX = "haizhi.";

    //DC
    public static final String DC_METADATA = PREFIX + "dc.metadata";
    public static final String DC_STORE = PREFIX + "dc.store";

    //SYS
    public static final String SYS_LOGIN_USER = PREFIX + "sys.login.user";
}
