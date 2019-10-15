package com.haizhi.graph.tag.analytics.engine.result;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.engine.flow.tools.es.EsBulkLoader;
import com.haizhi.graph.server.api.es.index.bean.Source;
import com.haizhi.graph.tag.analytics.bean.TagResult;
import com.haizhi.graph.tag.analytics.bean.TagValue;
import com.haizhi.graph.tag.analytics.util.Constants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by chengmo on 2018/3/19.
 */
@Component
public class TagResultsConsumer {

    private static final GLog LOG = LogFactory.getLogger(TagResultsConsumer.class);
    private static final String TAG_VALUE = TagValue._schema;
    private static final String TAG_SUFFIX = Constants.TAG_SUFFIX;

    @Autowired
    private EsBulkLoader esBulkLoader;

    @KafkaListener(topics = "${tag.analytics.kafka.topic}", containerFactory = "batchFactory")
    public void listen(List<ConsumerRecord<?, ?>> records, Acknowledgment ack) {
        try {
            Map<String, List<Source>> graphMap = new LinkedHashMap<>();
            for (ConsumerRecord<?, ?> record : records) {
                String message = Objects.toString(record.value());
                String[] arr = message.split(FKeys.SEPARATOR_001);
                if (arr.length != 2){
                    LOG.warn("invalid message [{0}]", message);
                    continue;
                }
                String graph = arr[0];
                List<TagResult> results = JSON.parseArray(arr[1], TagResult.class);

                // source list
                List<Source> sourceList = graphMap.get(graph);
                if (sourceList == null) {
                    sourceList = new ArrayList<>();
                    graphMap.put(graph, sourceList);
                }
                for (TagResult result : results) {
                    Source source = new Source();
                    source.setId(result.getTagId() + "#" + result.getObjectKey());
                    Map<String, Object> map = JSONUtils.toMap(result);
                    source.setSource(map);
                    sourceList.add(source);
                }
            }

            // esBulkLoader
            for (Map.Entry<String, List<Source>> entry : graphMap.entrySet()) {
                String indexName = entry.getKey() + TAG_SUFFIX;
                EsBulkLoader.EsBulkSource esBulkSource = new EsBulkLoader.EsBulkSource(indexName, TAG_VALUE);
                esBulkSource.setSources(entry.getValue());
                esBulkLoader.addBulkSource(esBulkSource);
            }
            boolean success = esBulkLoader.waitForCompletion();
            LOG.info("Consumer records[{0}], success={1}", records.size(), success);
        } catch (Exception e) {
            LOG.error(e);
        } finally {
            ack.acknowledge();
        }
    }
}
