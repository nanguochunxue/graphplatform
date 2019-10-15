package com.haizhi.graph.engine.flow.tools.kafka;

import com.haizhi.graph.common.util.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by chengmo on 2018/3/13.
 */
public class KafkaConfig {

    private static final String PREFIX = "spring.kafka.";
    private static final String BOOTSTRAP_SERVERS = "spring.kafka.bootstrap-servers";
    private static final String PREFIX_PRODUCER = "spring.kafka.producer.";
    private static final String PREFIX_PROPERTIES = "spring.kafka.properties.";

    public static ProducerFactory<String, String> producerFactory(String locationConfig) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : getConfigs(locationConfig).entrySet()) {
            String key = getProducerKey(entry.getKey());
            if (key.isEmpty()) {
                continue;
            }
            map.put(key, entry.getValue());
        }
        return new DefaultKafkaProducerFactory<>(map);
    }

    private static String getProducerKey(String key) {
        if (key.startsWith(BOOTSTRAP_SERVERS)) {
            key = StringUtils.substringAfter(key, PREFIX);
        } else if (key.startsWith(PREFIX_PRODUCER)) {
            key = StringUtils.substringAfter(key, PREFIX_PRODUCER);
        } else if (key.startsWith(PREFIX_PROPERTIES)) {
            key = StringUtils.substringAfter(key, PREFIX_PROPERTIES);
        } else {
            key = "";
        }
        return key.replaceAll("-", ".");
    }

    private static Map<String, Object> getConfigs(String fileName) {
        Properties props = PropertiesUtils.load("/" + fileName);
        return (Map) props;
    }
}
