package com.haizhi.graph.server.api.es.search;

import lombok.Data;

/**
 * Created by tanghaiyang on 2019/7/2.
 */
@Data
public class EsProxy {
    private String graph;
    private String requestMethod;
    private String uri;
    private String content;
}
