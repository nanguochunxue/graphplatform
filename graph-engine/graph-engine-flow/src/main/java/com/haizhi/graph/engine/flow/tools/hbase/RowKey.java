package com.haizhi.graph.engine.flow.tools.hbase;

import com.haizhi.graph.common.key.KeyFactory;
import com.haizhi.graph.common.key.KeyHasher;

import java.text.MessageFormat;

/**
 * Created by chengmo on 2018/4/3.
 */
public class RowKey {

    private static KeyHasher KEY_HASHER = KeyFactory.createKeyHasher();

    public static String get(long tagId, String objectKey) {
        String hash = KEY_HASHER.getPartitionHash(objectKey);
        return MessageFormat.format("{0}#{1}#{2}", hash, tagId + "", objectKey);
    }

    public static String get(long tagId, String statTime, String objectKey) {
        String hash = KEY_HASHER.getPartitionHash(objectKey);
        return MessageFormat.format("{0}#{1}#{2}#{3}", hash, tagId + "", statTime, objectKey);
    }
}
