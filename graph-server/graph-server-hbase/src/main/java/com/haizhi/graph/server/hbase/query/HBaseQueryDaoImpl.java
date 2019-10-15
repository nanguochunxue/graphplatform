package com.haizhi.graph.server.hbase.query;

import com.haizhi.graph.common.concurrent.executor.AsyncJoinExecutor;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.admin.bean.ColumnType;
import com.haizhi.graph.server.api.hbase.query.HBaseQueryDao;
import com.haizhi.graph.server.api.hbase.query.bean.*;
import com.haizhi.graph.server.hbase.client.HBaseClient;
import com.haizhi.graph.server.hbase.util.BytesBuilder;
import com.haizhi.graph.server.hbase.util.CloseableUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.DoubleColumnInterpreter;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.coprocessor.ColumnInterpreter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by chengmo on 2018/2/5.
 */
@Repository
public class HBaseQueryDaoImpl implements HBaseQueryDao {

    private static final GLog LOG = LogFactory.getLogger(HBaseQueryDaoImpl.class);

    @Autowired
    private HBaseClient hBaseClient;

    @Override
    public List<Map<String, String>> rangeScanQuery(StoreURL storeURL, HBaseRangeQuery rangeQuery) {
        if (rangeQuery == null) {
            LOG.error("It cannot search with a empty HBaseRangeQuery.");
            return Collections.emptyList();
        }
        List<Map<String, String>> results = new ArrayList<>();
        Table hTable = null;
        try {
            hTable = this.getConnection(storeURL)
                    .getTable(TableName.valueOf(rangeQuery.getTableName()));
            Scan scan = new Scan();
            scan.setStartRow(Bytes.toBytes(rangeQuery.getStartRow()));
            scan.setStopRow(Bytes.toBytes(rangeQuery.getStopRow()));
            scan.setCaching(2000);
            scan.setCacheBlocks(false);
            // results
            ResultScanner scanner = hTable.getScanner(scan);
            for (Result result : scanner) {
                Map<String, String> row = new HashMap<>();
                for (Cell cell : result.listCells()) {
                    row.put(Bytes.toString(CellUtil.cloneQualifier(cell)),
                            BytesBuilder.deserialize(CellUtil.cloneValue(cell)));
                }
                results.add(row);
            }
            scanner.close();
        } catch (Exception e) {
            LOG.error(e);
        } finally {
            CloseableUtils.close(hTable);
        }
        return results;
    }

    @Override
    public List<Map<String, Object>> getByRowKeys(StoreURL storeURL, String tableName, String... rowKeys) {
        return this.getByRowKeys(storeURL, tableName, new HashSet<>(Arrays.asList(rowKeys)));
    }

    @Override
    public Map<String, Object> aggregationQuery(StoreURL storeURL, HBaseRangeQuery rangeQuery) {
        if (rangeQuery == null) {
            LOG.error("It cannot search with a empty HBaseRangeQuery.");
            return Collections.emptyMap();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        /*AggregationClient aggClient = null;
        try {
            Configuration conf = this.getConnection(storeURL).getConfiguration();
            aggClient = new AggregationClient(conf);
            TableName tableName = TableName.valueOf(rangeQuery.getTable());
            String startRow = rangeQuery.getStartRow();
            String stopRow = rangeQuery.getStopRow();

            // aggregations
            for (Aggregation agg : rangeQuery.getAggregations().values()) {
                Scan scan = this.createScan(startRow, stopRow);
                scan.addColumn(Bytes.toBytes(Keys.OBJECTS), Bytes.toBytes(agg.getColumn()));
                Stats stats = agg.getStats();
                ColumnInterpreter interpreter = this.getColumnInterpreter(agg.getColumnType(), stats);
                Object aggResult = null;
                switch (stats) {
                    case COUNT:
                        aggResult = aggClient.rowCount(tableName, interpreter, scan);
                        break;
                    case MIN:
                        aggResult = aggClient.min(tableName, interpreter, scan);
                        break;
                    case MAX:
                        aggResult = aggClient.max(tableName, interpreter, scan);
                        break;
                    case AVG:
                        aggResult = aggClient.avg(tableName, interpreter, scan);
                        break;
                    case SUM:
                        aggResult = aggClient.sum(tableName, interpreter, scan);
                        break;
                }
                aggResult = aggResult == null ? 0 : aggResult;
                result.put(agg.getKey(), aggResult);
            }
        } catch (Throwable e) {
            LOG.error(e);
        } finally {
            CloseableUtils.close(aggClient);
        }*/
        return result;
    }

    @Override
    public HBaseQueryResult searchByKeys(StoreURL storeURL, HBaseQuery hBaseQuery){
        if (hBaseQuery == null) {
            LOG.error("It cannot search with a empty HBaseQuery.");
            return new HBaseQueryResult();
        }
        HBaseQueryResult hBaseResult = new HBaseQueryResult();
        try {
            AsyncJoinExecutor<TableResult> joinExecutor = new AsyncJoinExecutor();
            String namespace = hBaseQuery.getDatabase();
            for (Map.Entry<String, Set<String>> entry : hBaseQuery.getSchemas().entrySet()) {
                String tableName = entry.getKey();
                String fullName = namespace + ":" + tableName;
                joinExecutor.join(tableName, () -> {
                    return getResultByRowKeys(storeURL, fullName, entry.getValue());
                });
            }
            Map<String, TableResult> results = joinExecutor.actionGet();
            hBaseResult.setResults(results);
        } catch (Exception e) {
            LOG.error(e);
        }
        return hBaseResult;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private Connection getConnection(StoreURL storeURL) {
        return hBaseClient.getConnection(storeURL);
    }

    private TableResult getResultByRowKeys(StoreURL storeURL, String fullName, Set<String> rowKeys) {
        TableResult tableResult = new TableResult();
        tableResult.setTableName(fullName);
        tableResult.setRows(this.getByRowKeys(storeURL, fullName, rowKeys));
        return tableResult;
    }

    private List<Map<String, Object>> getByRowKeys(StoreURL storeURL, String fullName, Set<String> rowKeys) {
        List<Map<String, Object>> rows = new ArrayList<>();
        Table hTable = null;
        try {
            Connection connection = this.getConnection(storeURL);
            hTable = connection.getTable(TableName.valueOf(fullName));
            List<Get> gets = rowKeys.stream().map(rowKey ->
                    new Get(Bytes.toBytes(rowKey))).collect(Collectors.toList());
            Result[] results = hTable.get(gets);
            if (results == null || results.length == 0) {
                return rows;
            }

            // results
            for (Result result: results) {
                Map<String, Object> row = new HashMap<>();
                for (int j = 0; j < result.rawCells().length; j++) {
                    Cell cell = result.rawCells()[j];
                    row.put(Bytes.toString(CellUtil.cloneQualifier(cell)),
                            BytesBuilder.deserialize(CellUtil.cloneValue(cell)));
                }
                if (!row.isEmpty()){
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            LOG.error(e);
        } finally {
            CloseableUtils.close(hTable);
        }
        return rows;
    }

    private Scan createScan(String startRow, String stopRow) {
        Scan scan = new Scan();
        if (startRow != null) {
            scan.setStartRow(Bytes.toBytes(startRow));
        }
        if (startRow != null) {
            scan.setStopRow(Bytes.toBytes(stopRow));
        }
        scan.setCaching(1000);
        return scan;
    }

    private ColumnInterpreter getColumnInterpreter(ColumnType type, Stats stats) {
        switch (stats) {
            case COUNT:
                return new LongColumnInterpreter();
        }
        switch (type) {
            case LONG:
                return new LongColumnInterpreter();
            case DOUBLE:
                return new DoubleColumnInterpreter();
        }
        return null;
    }
}
