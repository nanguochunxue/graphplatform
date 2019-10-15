package com.haizhi.graph.server.api.gdb.search.aggregation;

import com.haizhi.graph.server.api.gdb.search.xcontent.ToXContent;

/**
 * Created by chengmo on 2018/1/24.
 */
public interface AggBuilder extends ToXContent {

    AggBuilder subAggregation(AggBuilder aggBuilder);
}
