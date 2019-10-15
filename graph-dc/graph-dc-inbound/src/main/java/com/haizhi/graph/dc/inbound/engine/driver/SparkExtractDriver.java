package com.haizhi.graph.dc.inbound.engine.driver;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.model.v1.Response;
import com.haizhi.graph.common.rest.RestFactory;
import com.haizhi.graph.common.rest.RestService;
import com.haizhi.graph.dc.core.constant.DcConstants;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.core.constant.Constants;
import com.haizhi.graph.dc.inbound.engine.conf.DcFlowTask;
import com.haizhi.graph.engine.flow.action.LauncherDriver;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.engine.flow.tools.hdfs.HDFSHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.collection.Iterator;

import java.util.*;

/**
 * Created by chengmo on 2019/2/11.
 */
public class SparkExtractDriver extends LauncherDriver {

    public static final int BATCH_SIZE = Constants.BATCH_SIZE;

    public static void main(String[] args) throws Exception {
        run(SparkExtractDriver.class, args);
    }

    /**
     * @param args args[0]  DcFlowTask to json string
     * @throws Exception
     */
    @Override
    protected void run(String[] args) throws Exception {
        DcFlowTask task = this.getFlowTask(args);
        SparkSession spark = this.getOrCreate(task, true);
        readAndCallInboundApi(task, spark);
        spark.close();
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private void readAndCallInboundApi(DcFlowTask task, SparkSession spark) {
        task.getSource().forEach(source -> {
            Dataset<Row> rows = null;
            switch (task.getTaskType()) {
                case HDFS:
                case FILE:
                    rows = spark.read().json(source.split(","));
                    break;
                case HIVE:
                    rows = spark.sql(source);
                    break;
            }
            if (this.debug) {
                rows.takeAsList(2).forEach(row -> {
                    System.out.println("data sample :" + row);
                });
            }
            System.out.println("data count : " + rows.count());
            rows.foreachPartition(rowIterator -> {
                RestService restService = RestFactory.getRestService();
                List<Map<String, Object>> dataRows = new LinkedList<>();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    StructType structType = row.schema();
                    Iterator<StructField> ite = structType.iterator();
                    Map<String, Object> singleRowData = new HashMap<>();
                    while (ite.hasNext()) {
                        String fieldName = ite.next().name();
                        Object fieldValue = row.getAs(fieldName);
                        singleRowData.put(fieldName, fieldValue);
                    }
                    dataRows.add(singleRowData);
                    if (dataRows.size() >= BATCH_SIZE) {
                        doSendBatch(restService, task, dataRows);
                    }
                }
                if (dataRows.size() !=0 ){
                    doSendBatch(restService, task, dataRows);
                }
            });
        });
    }

    private void doSendBatch(RestService restService, DcFlowTask task, List<Map<String, Object>> dataRows) {
        DcInboundDataSuo suo = new DcInboundDataSuo();
        suo.setRows(dataRows);
        suo.setSchema(task.getSchema());
        suo.setGraph(task.getGraph());
        suo.setOperation(task.getOperation());
        DcInboundDataSuo.Header header = new DcInboundDataSuo.Header();
        suo.setHeader(header);
        Map<String, Object> options = header.getOptions();
        options.put(DcConstants.KEY_TASK_ID, task.getTaskId());
        options.put(DcConstants.KEY_TASK_INSTANCE_ID, task.getInstanceId());
        doCallInboundApi(restService, task.getInboundApiUrl(), suo);
        dataRows.clear(); // http call is sync, so just clear
    }

    private void doCallInboundApi(RestService restService, String url, DcInboundDataSuo suo) {
        try {
            Response response = restService.doPost(url, suo, Response.class);
            System.out.println("response " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DcFlowTask getFlowTask(String[] args) {
        if (args.length < 1) {
            System.err.println(Arrays.asList(args));
            throw new IllegalArgumentException("args.length < 1");
        }
        System.out.println("args=" + Arrays.asList(args));
        String appArgs = args[0];
        System.out.println(appArgs);

        // task
        HDFSHelper helper = new HDFSHelper();
        String path = StringUtils.substringBefore(appArgs, FKeys.SEPARATOR_002);
        DcFlowTask task = JSON.parseObject(path, DcFlowTask.class);
        //DcFlowTask task = JSON.parseObject(helper.readLine(path), DcFlowTask.class);
        JSONUtils.println(task);
        this.debug = task.isDebugEnabled();
        this.security = task.isSecurityEnabled();
        return task;
    }
}
