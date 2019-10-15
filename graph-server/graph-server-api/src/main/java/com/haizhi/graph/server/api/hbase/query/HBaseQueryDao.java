package com.haizhi.graph.server.api.hbase.query;

import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.query.bean.HBaseQuery;
import com.haizhi.graph.server.api.hbase.query.bean.HBaseQueryResult;
import com.haizhi.graph.server.api.hbase.query.bean.HBaseRangeQuery;

import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/2/5.
 */
public interface HBaseQueryDao {

    /**
     * Scan data with a rowKey range.
     *
     * @param rangeQuery
     * @return
     */
    List<Map<String, String>> rangeScanQuery(StoreURL storeURL, HBaseRangeQuery rangeQuery);

    /**
     * Scan data with rowKeys.
     *
     * @param tableName namespace:collection
     * @param storeURL
     * @param rowKeys
     * @return
     */
    List<Map<String, Object>> getByRowKeys(StoreURL storeURL, String tableName, String... rowKeys);

    /**
     * Aggregation query with a rowKey range.
     *
     * @param rangeQuery
     * @return <aggregation-key, aggregation-value>
     */
    Map<String, Object> aggregationQuery(StoreURL storeURL, HBaseRangeQuery rangeQuery);

    HBaseQueryResult searchByKeys(StoreURL storeURL, HBaseQuery hBaseQuery);

}
