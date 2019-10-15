package com.haizhi.graph.tag.analytics.engine.driver.handler;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.util.CodecUtils;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.engine.flow.sql.Projections;
import com.haizhi.graph.engine.flow.tools.hbase.HBaseContext;
import com.haizhi.graph.engine.flow.tools.hbase.RowKey;
import com.haizhi.graph.engine.flow.tools.kafka.KafkaHelper;
import com.haizhi.graph.engine.flow.util.DataUtils;
import com.haizhi.graph.engine.flow.util.StrPatternUtils;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.admin.bean.HBaseRows;
import com.haizhi.graph.tag.analytics.bean.TagValue;
import com.haizhi.graph.tag.analytics.bean.TagValueDaily;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTask;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;

import java.util.*;
import java.util.regex.Matcher;

/**
 * Created by chengmo on 2018/4/18.
 */
public class StatResultHandler {

    /**
     * @param resultRdd
     * @param tagId
     * @param tableName
     */
    public static void persistOnFirstStage(JavaRDD<Row> resultRdd, long tagId, String tableName){
        resultRdd.foreachPartition(rowIterator -> {
            if (!rowIterator.hasNext()){
                //return;
            }
            int count = 0;
            boolean pageBulk = false;
            HBaseContext context = new HBaseContext();
            HBaseRows hBaseRows = new HBaseRows();
            StoreURL storeURL = new StoreURL();
            while (rowIterator.hasNext()) {
                count++;
                Row row = rowIterator.next();
                hBaseRows.addRow(getRowMapOnFirstStage(row, tagId));
                if (count % 5000 == 0){
                    pageBulk = true;
                }
                if (!pageBulk){
                    continue;
                }
                context.bulkUpsert(storeURL, tableName, hBaseRows);
                hBaseRows.clear();
                count = 0;
            }
            if (count > 0 && count < 5000){
                context.bulkUpsert(storeURL, tableName, hBaseRows);
                hBaseRows.clear();
            }
        });
    }

    /**
     * @param resultRdd
     * @param tagId
     * @param tableName
     * @param task
     */
    public static void persistOnSecondStage(JavaRDD<Row> resultRdd, long tagId, String tableName, FlowTask task){
        String graphName = task.getGraph();
        String kafkaTopic = task.getKafkaTopic();
        String kafkaLocation = task.getLocationConfig();
        StoreURL storeURL = new StoreURL();
        resultRdd.foreachPartition(rowIterator -> {
            int count = 0;
            boolean pageBulk = false;
            HBaseContext context = new HBaseContext();
            HBaseRows hBaseRows = new HBaseRows();
            KafkaHelper kafkaHelper = new KafkaHelper(kafkaLocation);
            List<Object> kafkaRows = new ArrayList<>();
            while (rowIterator.hasNext()) {
                count++;
                Row row = rowIterator.next();
                Map<String, Object> rowMap = getRowMapOnSecondStage(row, tagId);
                hBaseRows.addRow(rowMap);
                kafkaRows.add(rowMap);
                if (count % 1000 == 0){
                    pageBulk = true;
                }
                if (!pageBulk){
                    continue;
                }
                context.bulkUpsert(storeURL,tableName, hBaseRows);
                String message = graphName + FKeys.SEPARATOR_001 + JSON.toJSONString(kafkaRows);
                kafkaHelper.send(kafkaTopic, message);
                hBaseRows.clear();
                count = 0;
            }
            if (count > 0 && count < 1000){
                context.bulkUpsert(storeURL, tableName, hBaseRows);
                String message = graphName + FKeys.SEPARATOR_001 + JSON.toJSONString(kafkaRows);
                kafkaHelper.send(kafkaTopic, message);
                hBaseRows.clear();
            }
        });
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private static Map<String, Object> getRowMapOnFirstStage(Row row, long tagId){
        // Projections
        Map<String, Object> result = new HashMap<>();
        String name = row.getAs(Projections.name);
        String objectKey = CodecUtils.md5(name);
        String statTime = row.getAs(TagValueDaily.statTime);
        //DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")
        result.put(Keys._ROW_KEY, RowKey.get(tagId, statTime, objectKey));
        result.put(TagValueDaily.tagId, String.valueOf(tagId));
        result.put(TagValueDaily.objectKey, objectKey);
        result.put(TagValueDaily.statTime, statTime);

        // value
        addValueOrNested(row, result);
        return result;
    }

    private static Map<String, Object> getRowMapOnSecondStage(Row row, long tagId){
        // Projections
        Map<String, Object> result = new HashMap<>();
        String objectKey = row.getAs(TagValue.objectKey);
        result.put(Keys._ROW_KEY, RowKey.get(tagId, objectKey));
        result.put(TagValue.tagId, tagId);
        result.put(TagValue.objectKey, objectKey);
        result.put(TagValue.updateTime, new Date());

        // value
        addValueOrNested(row, result);
        return result;
    }

    private static void addValueOrNested(Row row, Map<String, Object> rowMap){
        StructType type = row.schema();
        String[] fieldNames = type.fieldNames();
        Map<String, Object> valueMap = new HashMap<>();
        for (int i = 0; i < fieldNames.length; i++) {
            String fieldName = fieldNames[i];
            if (!Projections.contains(fieldName)){
                Object obj = row.getAs(fieldName);
                if (obj instanceof String){
                    obj = Objects.toString(obj, "");
                    obj = DataUtils.parseObject(obj.toString(), FieldType.DOUBLE.name());
                }
                Matcher matcher = StrPatternUtils.matcher(fieldName);
                if (matcher.find()){
                    fieldName = matcher.group();
                    fieldName = StringUtils.substringAfter(fieldName, ".");
                }
                valueMap.put(fieldName, obj);
            }
        }
        if (valueMap.size() < 0){
            String str = Objects.toString(valueMap.values().iterator().next(), "0");
            rowMap.put(Projections.value, str);
            rowMap.put(Projections.dataType, FieldType.DOUBLE.name());
        } else {
            rowMap.put(Projections.value, JSON.toJSONString(valueMap));
            rowMap.put(Projections.dataType, "MAP");
        }
    }
}
