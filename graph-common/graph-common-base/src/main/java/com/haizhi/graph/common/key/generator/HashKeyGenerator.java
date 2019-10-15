package com.haizhi.graph.common.key.generator;

import com.haizhi.graph.common.key.KeyFactory;
import com.haizhi.graph.common.key.KeyUtils;
import com.haizhi.graph.common.key.format.KeyFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.MessageFormat;

/**
 * Created by chengmo on 2017/12/7.
 */
public class HashKeyGenerator implements KeyGenerator {

    private static final int DEFAULT_MAX_PARTITIONS = KeyUtils.LOGIC_PARTITIONS;

    /* Minimum Integer digits */
    private static final int HASH_DIGITS = KeyUtils.HASH_DIGITS;

    private KeyFormatter keyFormatter;

    private long graphId;
    private long schemaSeq;
    private int maxPartitions;

    private String _hash;
    private long _key;
    private long _from;
    private long _to;

    public HashKeyGenerator(long graphId, long schemaSeq) {
        this(graphId, schemaSeq, DEFAULT_MAX_PARTITIONS, KeyFactory.createFormatter());
    }

    public HashKeyGenerator(long graphId, long schemaSeq, int maxPartitions, KeyFormatter keyFormatter) {
        this.graphId = graphId;
        this.schemaSeq = schemaSeq;
        if (maxPartitions < 1 || maxPartitions > DEFAULT_MAX_PARTITIONS) {
            this.maxPartitions = DEFAULT_MAX_PARTITIONS;
        } else {
            this.maxPartitions = maxPartitions;
        }
        this.keyFormatter = keyFormatter;
    }

    @Override
    public String getRowKeyByLongKey(String longKeyStr) {
        long longKey = NumberUtils.toLong(longKeyStr, 0);
        if (longKey == 0){
            return "";
        }
        long remainder = Long.remainderUnsigned(longKey, maxPartitions);
        String hash = KeyUtils.formatPlaceHolder(remainder, HASH_DIGITS);
        return format("{0}#{1}", hash, longKeyStr);
    }

    @Override
    public String getRowKey(String objectKey) {
        return this.getRowKey(objectKey, "");
    }

    private String getRowKey(String objectKey, String createTime) {
        this._key = keyFormatter.toLong(graphId, schemaSeq, objectKey);
        this.initializePartitionHash(_key);
        if (StringUtils.isNotBlank(createTime)) {
            return format("{0}#{1}#{2}", _hash, createTime, toL19(_key));
        }
        return format("{0}#{1}", _hash, toL19(_key));
    }

    @Override
    public String getRowKey(String objectKey, String fromKey, String toKey) {
        return this.getRowKey(objectKey, fromKey, toKey, "");
    }

    @Override
    public String getRowKey(String objectKey, String fromKey, String toKey, String createTime) {
        this._key = keyFormatter.toLong(graphId, schemaSeq, objectKey);

        // _from
        long fromSchemaSeq = NumberUtils.toLong(StringUtils.substringBefore(fromKey, "/"));
        this._from = keyFormatter.toLong(graphId, fromSchemaSeq, StringUtils.substringAfter(fromKey, "/"));

        // _to
        long toSchemaSeq = NumberUtils.toLong(StringUtils.substringBefore(fromKey, "/"));
        this._to = keyFormatter.toLong(graphId, toSchemaSeq, StringUtils.substringAfter(toKey, "/"));

        // _hash
        this.initializePartitionHash(_from);

        // row key
        if (StringUtils.isBlank(createTime)) {
            return format("{0}#{1}~{2}#{3}", _hash, toL19(_from), toL19(_to), toL19(_key));
        }
        return format("{0}#{1}~{2}#{3}#{4}", _hash, toL19(_from), toL19(_to), createTime, toL19(_key));
    }

    @Override
    public String getScanRowKey(String fromKey, String toKey, String timestamp) {
        return getSummaryRowKey(fromKey, toKey, timestamp);
    }

    @Override
    public String getSummaryRowKey(String fromKey, String toKey, String timeRange) {
        // _from
        long fromSchemaSeq = NumberUtils.toLong(StringUtils.substringBefore(fromKey, "/"));
        this._from = keyFormatter.toLong(graphId, fromSchemaSeq, StringUtils.substringAfter(fromKey, "/"));

        // _to
        long toSchemaSeq = NumberUtils.toLong(StringUtils.substringBefore(fromKey, "/"));
        this._to = keyFormatter.toLong(graphId, toSchemaSeq, StringUtils.substringAfter(toKey, "/"));

        // _hash
        this.initializePartitionHash(_from);

        // row key
        return format("{0}#{1}~{2}#{3}", _hash, toL19(_from), toL19(_to), timeRange);
    }

    @Override
    public Long getGraphId() {
        return graphId;
    }

    @Override
    public Long getSchemaSeq() {
        return schemaSeq;
    }

    private String toL19(long key) {
        return keyFormatter.format19Digit(key);
    }

    private String format(String pattern, Object... arguments) {
        return MessageFormat.format(pattern, arguments);
    }

    private void initializePartitionHash(long longKey) {
        long remainder = Long.remainderUnsigned(longKey, maxPartitions);
        this._hash = KeyUtils.formatPlaceHolder(remainder, HASH_DIGITS);
    }
}
