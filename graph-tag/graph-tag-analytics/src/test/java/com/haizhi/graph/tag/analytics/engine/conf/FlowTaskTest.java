package com.haizhi.graph.tag.analytics.engine.conf;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

/**
 * Created by chengmo on 2018/4/3.
 */
public class FlowTaskTest {

    @Test
    public void example(){
        FlowTask task = FlowTaskFactory.get("crm_dev");
        System.out.println(JSON.toJSONString(task, true));
    }
}
