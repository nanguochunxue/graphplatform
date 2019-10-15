package com.haizhi.graph.server.api.constant;

import com.google.common.collect.Lists;

import java.util.ArrayList;

/**
 * Created by tanghaiyang on 2019/6/6.
 */
public class RequestMethod {
    public static final String PUT = "PUT";
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String HEAD = "HEAD";
    public static final String DELETE = "DELETE";

    public static final ArrayList AllMethod = Lists.newArrayList(PUT, POST, GET, HEAD, DELETE);
}
