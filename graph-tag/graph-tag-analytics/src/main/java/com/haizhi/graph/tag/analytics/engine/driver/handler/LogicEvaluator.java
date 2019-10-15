package com.haizhi.graph.tag.analytics.engine.driver.handler;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.engine.flow.tools.hbase.HBaseContext;
import com.haizhi.graph.engine.flow.tools.hbase.RowKey;
import com.haizhi.graph.engine.flow.tools.kafka.KafkaHelper;
import com.haizhi.graph.server.api.hbase.admin.bean.HBaseRows;
import com.haizhi.graph.tag.analytics.bean.TagResult;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTask;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTaskSchema;
import com.haizhi.graph.tag.analytics.engine.evaluator.TagsEvaluator;
import org.apache.spark.api.java.JavaPairRDD;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/4/18.
 */
public class LogicEvaluator {

    public static void evaluateAndPersist(JavaPairRDD<String, String> resultRdd, FlowTask task){
        evaluateAndPersist(resultRdd, task, new HashMap<>());
    }

    /**
     * @param resultRdd
     * @param task
     */
    public static void evaluateAndPersist(JavaPairRDD<String, String> resultRdd,
                                          FlowTask task, Map<String, String> globalCache) {
        if (resultRdd == null || resultRdd.isEmpty()) {
            System.out.println("ResultRdd is empty, return.");
            return;
        }
        String graphName = task.getGraph();
        String kafkaTopic = task.getKafkaTopic();
        String kafkaLocation = task.getLocationConfig();
        FlowTask.Stage stage = task.getStage(1);

        FlowTaskSchema sch = FlowTaskHandler.getHBaseSchema(stage.getOutput());
        String tableName = task.getGraph() + ":" + sch.getSchema();
        HBaseContext hBaseContext = new HBaseContext();
//        hBaseContext.createTableIfNotExists(task.getGraph(), sch.getSchema());

        // globalCache
        StringBuilder cacheBuilder = new StringBuilder();
        for (String str : globalCache.values()) {
            cacheBuilder.append(str).append(FKeys.SEPARATOR_003);
        }
        String globalCacheStr = cacheBuilder.toString();
        
        // debugging
        HBaseRows debugHBaseRows = new HBaseRows();
        String debugTagDomainPath = task.getProperties().get(TagsEvaluator.TAG_DOMAIN_KEY);
        TagsEvaluator debugTagsEvaluator = new TagsEvaluator(debugTagDomainPath);
        KafkaHelper debugKafkaHelper = new KafkaHelper(kafkaLocation);
        resultRdd.take(2).forEach(t -> {
            String key = t._1();
            String value = t._2();
            value = value + FKeys.SEPARATOR_003 + globalCacheStr;
            System.out.println("key=" + key);
            System.out.println("value=" + value);
            try {
                List<TagResult> results = debugTagsEvaluator.execute(key, value, task);
                System.out.println("results:\n" + JSON.toJSONString(results, true));
                /*if (!task.isDebugEnabled()) {
                    return;
                }*/
                if (results.isEmpty()) {
                    return;
                }
                for (TagResult tr : results) {
                    Map<String, Object> row = JSONUtils.toMap(tr);
                    row.put(Keys._ROW_KEY, RowKey.get(tr.getTagId(), tr.getObjectKey()));
                    debugHBaseRows.addRow(row);
                }

                // kafka
                String message = graphName + FKeys.SEPARATOR_001 + JSON.toJSONString(results);
                debugKafkaHelper.send(kafkaTopic, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
//        hBaseContext.bulkUpsert(tableName, debugHBaseRows);

        /*if (task.isDebugEnabled()) {
            return;
        }*/

        // result
        resultRdd.foreachPartition(tuple2Iterator -> {
            int count = 0;
            System.out.println("foreachPartition start...");
            boolean pageBulk = false;
            HBaseContext context = new HBaseContext();
            HBaseRows hBaseRows = new HBaseRows();
            System.out.println("success to initialize hbase context");
            KafkaHelper kafkaHelper = new KafkaHelper(kafkaLocation);
            System.out.println("success to initialize kafka helper");
            String tagDomainPath = task.getProperties().get(TagsEvaluator.TAG_DOMAIN_KEY);
            TagsEvaluator tagsEvaluator = new TagsEvaluator(tagDomainPath);
            List<Map<String, Object>> rows = new ArrayList<>();
            while (tuple2Iterator.hasNext()) {
                Tuple2<String, String> tuple2 = tuple2Iterator.next();
                String key = tuple2._1();
                String value = tuple2._2();
                value = value + FKeys.SEPARATOR_003 + globalCacheStr;
                List<TagResult> results = tagsEvaluator.execute(key, value, task);
                if (results.isEmpty()) {
                    continue;
                }
                for (TagResult tr : results) {
                    Map<String, Object> row = JSONUtils.toMap(tr);
                    row.put(Keys._ROW_KEY, RowKey.get(tr.getTagId(), tr.getObjectKey()));
                    rows.add(row);
                }
                count += results.size();
                if (count >= 1000) {
                    pageBulk = true;
                }
                if (!pageBulk) {
                    continue;
                }
                hBaseRows.addRows(rows);
//                context.bulkUpsert(tableName, hBaseRows);
                String message = graphName + FKeys.SEPARATOR_001 + JSON.toJSONString(rows);
                kafkaHelper.send(kafkaTopic, message);
                hBaseRows.clear();
                rows.clear();
                count = 0;
                pageBulk = false;
            }
            if (count > 0 && count < 1000) {
                hBaseRows.addRows(rows);
//                context.bulkUpsert(tableName, hBaseRows);
                String message = graphName + FKeys.SEPARATOR_001 + JSON.toJSONString(rows);
                kafkaHelper.send(kafkaTopic, message);
                hBaseRows.clear();
                rows.clear();
            }
        });
    }
}
