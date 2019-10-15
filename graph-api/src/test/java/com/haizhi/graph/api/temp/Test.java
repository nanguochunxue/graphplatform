package com.haizhi.graph.api.temp;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by tanghaiyang on 2019/6/25.
 */
public class Test {

    @org.junit.Test
    public void test(){
        String message ="" ;
        StringUtils.removeAll(message, "\"");
        System.out.println("11111");
    }
}
