package com.haizhi.graph.engine.flow.tools.hbase;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.engine.flow.util.DataUtils;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.admin.HBaseAdminDao;
import com.haizhi.graph.server.api.hbase.admin.bean.HBaseRows;
import com.haizhi.graph.server.hbase.admin.HBaseAdminDaoImpl;
import com.haizhi.graph.server.hbase.client.HBaseClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/3/14.
 */
public class HBaseContext {

    private Configuration conf;
    private SparkSession spark;
    private JavaSparkContext jsc;
    private HBaseAdminDao hBaseAdminDao;
    private static HBaseClient hBaseClient;

    public HBaseContext() {
        this(null, null);
    }

    public HBaseContext(SparkSession spark) {
        this(spark, null);
    }

    public HBaseContext(SparkSession spark, Configuration conf) {
        this.initialize();
        if (conf != null) {
            this.conf.addResource(conf);
        }
        this.spark = spark;
        if (spark != null) {
            this.jsc = JavaSparkContext.fromSparkContext(spark.sparkContext());
        }

        // HBaseAdminDao
        this.hBaseClient = new HBaseClient();
        Connection conn = hBaseClient.getConnection(new StoreURL());
        if (conn == null || conn.isClosed()) {
            // TODO: 2019/7/8
//            hBaseClient.connect(this.conf);
        }
        this.hBaseAdminDao = new HBaseAdminDaoImpl();
//        this.hBaseAdminDao.setHBaseClient(hBaseClient);
    }

    /**
     * Create database if it is not exists.
     *
     * @param database
     * @return
     */
    public boolean createDatabaseIfNotExists(StoreURL storeURL, String database) {
        if (hBaseAdminDao.existsDatabase(storeURL, database)) {
            return true;
        }
        return hBaseAdminDao.createDatabase(storeURL, database);
    }

    /**
     * Create table if it is not exists.
     *
     * @param database
     * @param table
     * @return
     */
    public boolean createTableIfNotExists(StoreURL storeURL, String database, String table) {
        if (hBaseAdminDao.existsTable(storeURL, database, table)) {
            return true;
        }
        return hBaseAdminDao.createTable(storeURL, database, table, true);
    }

    /**
     * Bulk update or insert rows.
     *
     * @param tableName
     * @param rows
     * @return
     */
    public CudResponse bulkUpsert(StoreURL storeURL, String tableName, HBaseRows rows) {
        if (!tableName.contains(":")){
            return new CudResponse();
        }
        String[] arr = tableName.split(":");
        return hBaseAdminDao.bulkUpsert(storeURL, arr[0], arr[1], rows);
    }

    /**
     * Bulk update or insert rows.
     *
     * @param database
     * @param table
     * @param rows
     * @return
     */
    public CudResponse bulkUpsert(StoreURL storeURL, String database, String table, HBaseRows rows) {
        return hBaseAdminDao.bulkUpsert(storeURL, database, table, rows);
    }

    /**
     * Deletes by rowKey scan.
     *
     * @param database
     * @param table
     * @param startRow
     * @param stopRow
     */
    public CudResponse deleteByScan(StoreURL storeURL, String database, String table, String startRow, String stopRow) {
        return hBaseAdminDao.deleteByScan(storeURL, database, table, startRow, stopRow);
    }

    /**
     * Gets spark SQL rows RDD with scan.
     *
     * @param hBaseScan
     * @return
     * @throws IOException
     */
    public JavaRDD<Row> scanRDD2Row(HBaseScan hBaseScan) throws IOException {
        JavaPairRDD<ImmutableBytesWritable, Result> rdd = this.getResults(hBaseScan);
        Map<String, String> fields = hBaseScan.getFields();
        JavaRDD<Row> resultRdd = rdd.map(t -> {
            Result result = t._2();
            List<String> values = new ArrayList<>();

            // rowKey
            values.add(Bytes.toString(result.getRow()));
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                String field = entry.getKey();
                String fieldType = entry.getValue();
                String[] arr = field.split(":");
                String value = Bytes.toString(result.getValue(Bytes.toBytes(arr[0]), Bytes.toBytes(arr[1])));
                Object objValue = DataUtils.parseObject(value, fieldType);
                values.add(String.valueOf(objValue));
            }
            return RowFactory.create(values.toArray());
        });
        return resultRdd;
    }

    /**
     * Query HBase table rows with scan.
     *
     * @param hBaseScan
     * @return
     * @throws IOException
     */
    public JavaPairRDD<String, String> scanRDD2JSON(HBaseScan hBaseScan) throws
            IOException {
        String tableName = hBaseScan.getTableName();
        Map<String, String> fields = hBaseScan.getFields();
        String[] keyArr = hBaseScan.getReduceKey().split(":");
        String keyFamily = keyArr[0];
        String keyQualifier = keyArr[1];

        // Obtain the data in the table through the Spark interface
        JavaPairRDD<ImmutableBytesWritable, Result> rdd = this.getResults(hBaseScan);

        // Returns <String=key, String=tableName+column1Value+column2Value...>
        JavaPairRDD<String, String> resultRDD = rdd.mapToPair(t -> {
            Result result = t._2();
            StringBuilder text = new StringBuilder();
            text.append(tableName).append(FKeys.SEPARATOR_002);
            String key = Bytes.toString(result.getValue(Bytes.toBytes(keyFamily), Bytes.toBytes(keyQualifier)));
            if (key.contains("/")) {
                key = StringUtils.substringAfter(key, "/");
            }
            Map<String, Object> row = new LinkedHashMap<>();

            for (Map.Entry<String, String> entry : fields.entrySet()) {
                String field = entry.getKey();
                String fieldType = entry.getValue();
                String[] arr = field.split(":");
                String value = Bytes.toString(result.getValue(Bytes.toBytes(arr[0]), Bytes.toBytes(arr[1])));

                Object objValue = DataUtils.parseObject(value, fieldType);
                row.put(arr[1], objValue);
            }
            text.append(JSON.toJSONString(row));
            return new Tuple2(key, text.toString());
        });
        return resultRDD;
    }


    ///////////////////////
    // private functions
    ///////////////////////
    private JavaPairRDD<ImmutableBytesWritable, Result> getResults(HBaseScan hBaseScan) throws IOException {
        String tableName = hBaseScan.getTableName();
        Map<String, String> fields = hBaseScan.getFields();
        String startRow = hBaseScan.getStartRow();
        String stopRow = hBaseScan.getStopRow();

        // Declare the information of the table to be queried.
        Scan scan = new Scan();
        String reduceKey = hBaseScan.getReduceKey();
        if (StringUtils.isNotBlank(reduceKey)) {
            String[] keyArr = hBaseScan.getReduceKey().split(":");
            String keyFamily = keyArr[0];
            String keyQualifier = keyArr[1];
            scan.addColumn(Bytes.toBytes(keyFamily), Bytes.toBytes(keyQualifier));
        }
        for (String field : fields.keySet()) {
            String[] arr = field.split(":");
            scan.addColumn(Bytes.toBytes(arr[0]), Bytes.toBytes(arr[1]));
        }
        if (StringUtils.isNoneBlank(startRow, stopRow)) {
            scan.setStartRow(Bytes.toBytes(startRow));
            scan.setStopRow(Bytes.toBytes(stopRow));
        }
        conf.set(TableInputFormat.INPUT_TABLE, tableName);
        conf.set(TableInputFormat.SCAN, scanToString(scan));

        // Obtain the data in the table through the Spark interface
        return jsc.newAPIHadoopRDD(conf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
    }

    /**
     * 只扫描hbase rowkey
     *
     * @param tableName
     * @return
     * @throws IOException
     */
    public JavaRDD<String> getScanByKeyFilter(String tableName) throws IOException {
        Scan scan = new Scan();
        scan.setFilter(new KeyOnlyFilter());

        conf.set(TableInputFormat.INPUT_TABLE, tableName);
        conf.set(TableInputFormat.SCAN, scanToString(scan));
        return jsc.newAPIHadoopRDD(conf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class)
                .map(t -> Bytes.toString(t._2().getRow()));
    }

    /**
     * 扫描特定列
     *
     * @param tableName
     * @param cf
     * @param fields
     * @return
     * @throws IOException
     */
    public JavaRDD<Result> getQfByScan(String tableName, String cf, List<String> fields) throws IOException {
        Scan scan = new Scan();
        for (String field : fields) {
            scan.addColumn(Bytes.toBytes(cf), Bytes.toBytes(field));
        }

        conf.set(TableInputFormat.INPUT_TABLE, tableName);
        conf.set(TableInputFormat.SCAN, scanToString(scan));
        return jsc.newAPIHadoopRDD(conf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class)
                .map(t -> t._2());
    }

    private String scanToString(Scan scan) throws IOException {
        // java.lang.NoSuchMethodError: org.apache.hadoop.hbase.client.Scan.setCaching(I)
        // Lorg/apache/hadoop/hbase/client/Scan;
        //scan.setCaching(1000);
        //scan.setCacheBlocks(false);
        return Base64.encodeBytes(ProtobufUtil.toScan(scan).toByteArray());
    }

    private void initialize() {
        this.conf = HBaseConfiguration.create();
        conf.setInt(HConstants.HBASE_RPC_TIMEOUT_KEY, 200000);
        conf.setInt(HConstants.HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, 200000);
        conf.setInt(HConstants.HBASE_CLIENT_OPERATION_TIMEOUT, 30000);
    }


    public Configuration getConf() {
        return conf;
    }

    public JavaSparkContext getJsc() {
        return jsc;
    }

}
