package com.haizhi.graph.tag.analytics.engine.alg;

import java.util.List;

/**
 * Created by wangxy on 2018/5/29.
 */


public class AlgFlowTask {
    /**
     * hbase namespace or hive database
     */
    public String graph;

    /**
     * 算法名
     */
    public String algName;

    /**
     * schema
     * <p>
     * key: tableName, value: fields
     */
    public List<AlgSchema> schema;

}
