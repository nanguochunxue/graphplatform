package com.haizhi.graph.tag.analytics.engine.driver.handler;

import com.haizhi.graph.tag.analytics.engine.conf.DataSourceType;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTaskOutput;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTaskSchema;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by chengmo on 2018/4/18.
 */
public class FlowTaskHandler {

    public static FlowTaskSchema getHBaseSchema(FlowTaskOutput output){
        List<FlowTaskSchema> list = output.getSchemas().values().stream()
                .filter(e -> e.getSourceType() == DataSourceType.HBASE)
                .collect(Collectors.toList());
        return list.get(0);
    }
}
