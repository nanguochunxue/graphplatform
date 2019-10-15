package com.haizhi.graph.common.key.generator;

/**
 * Created by chengmo on 2017/12/7.
 */
public interface KeyGenerator {

    String getRowKeyByLongKey(String longKeyStr);

    String getRowKey(String objectKey);

    String getRowKey(String objectKey, String fromKey, String toKey);

    String getRowKey(String objectKey, String fromKey, String toKey, String createTime);

    String getScanRowKey(String fromKey, String toKey, String timestamp);

    String getSummaryRowKey(String fromKey, String toKey, String dateRange);

    Long getGraphId();

    Long getSchemaSeq();
}
