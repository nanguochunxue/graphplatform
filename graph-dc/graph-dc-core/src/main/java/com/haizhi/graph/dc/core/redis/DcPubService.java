package com.haizhi.graph.dc.core.redis;

/**
 * Created by chengmo on 2019/6/25.
 */
public interface DcPubService {

    boolean publish(String message);
}
