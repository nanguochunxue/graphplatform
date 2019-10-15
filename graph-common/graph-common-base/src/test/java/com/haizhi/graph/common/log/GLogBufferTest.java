package com.haizhi.graph.common.log;

import com.haizhi.graph.common.json.JSONUtils;
import org.junit.Test;

/**
 * Created by chengmo on 2018/10/25.
 */
public class GLogBufferTest {

    @Test
    public void example(){
        GLogBuffer buffer = new GLogBuffer();
        buffer.setMaxNrLines(2);
        buffer.addLine("111111111");
        buffer.addLine("222222222");
        buffer.addLine("333333333");
        JSONUtils.println(buffer);
    }
}
