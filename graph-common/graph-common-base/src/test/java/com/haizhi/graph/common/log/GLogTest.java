package com.haizhi.graph.common.log;

import org.junit.Test;

/**
 * Created by chengmo on 2017/12/15.
 */
public class GLogTest {

    private static final GLog LOG = LogFactory.getLogger(GLogTest.class);

    @Test
    public void example(){
        // info
        LOG.info("userName={0},password={1}", "user1", "pwd");

        // error
        Throwable ex = new RuntimeException("test");
        LOG.error("userName={0},password={1}", ex, "user1", "pwd");
    }
}
