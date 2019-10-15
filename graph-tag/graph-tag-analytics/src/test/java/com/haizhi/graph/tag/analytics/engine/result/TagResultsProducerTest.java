package com.haizhi.graph.tag.analytics.engine.result;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.engine.flow.tools.kafka.KafkaHelper;
import com.haizhi.graph.tag.analytics.bean.TagResult;
import com.haizhi.graph.tag.core.domain.DataType;
import org.junit.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/3/19.
 */
public class TagResultsProducerTest {

    private static final String TOPIC = "tag-analytics-test";

    @Test
    public void send() throws Exception {
        String profile = "application-tag-hdp.properties";
        KafkaHelper helper = new KafkaHelper(profile);
        KafkaTemplate<String, String> kafkaTemplate = helper.kafkaTemplate();

        String message = "crm_dev" + FKeys.SEPARATOR_001 + JSON.toJSONString(this.getTagResults().values());
        SendResult<String, String> result = kafkaTemplate.send(TOPIC, message).get();
        System.out.println(result.getProducerRecord());
    }

    private Map<Long, TagResult> getTagResults(){
        Map<Long, TagResult> results = new LinkedHashMap<>();
        TagResult tagResult = new TagResult();
        tagResult.setTagId(3);
        tagResult.setObjectKey("111");
        tagResult.setDataType(DataType.ENUM);
        tagResult.setUpdateTime(new Date());
        tagResult.setValue("value1");
        results.put(tagResult.getTagId(), tagResult);

        tagResult = new TagResult();
        tagResult.setTagId(6);
        tagResult.setObjectKey("222");
        tagResult.setDataType(DataType.STRING);
        tagResult.setUpdateTime(new Date());
        tagResult.setValue("value1");
        results.put(tagResult.getTagId(), tagResult);

        return results;
    }
}
