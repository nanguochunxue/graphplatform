package com.haizhi.graph.server.hbase.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.IOUtils;

/**
 * Created by chengmo on 2018/5/14.
 */
public class BytesBuilder {

    private static final ParserConfig defaultConfig = new ParserConfig();

    static {
        defaultConfig.setAutoTypeSupport(true);
    }

    public static byte[] serialize(Object object) throws RuntimeException {
        if (object == null) {
            return new byte[0];
        }
        try {
            return JSON.toJSONBytes(object, SerializerFeature.WriteClassName);
        } catch (Exception e) {
            throw new RuntimeException("Could not serialize: " + e.getMessage(), e);
        }
    }

    public static <T> T deserialize(byte[] bytes) throws RuntimeException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return JSON.parseObject(new String(bytes, IOUtils.UTF8), Object.class, defaultConfig);
        } catch (Exception e) {
            throw new RuntimeException("Could not deserialize: " + e.getMessage(), e);
        }
    }
}
