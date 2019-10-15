package com.haizhi.graph.common.key.getter;

import com.haizhi.graph.common.key.*;
import com.haizhi.graph.common.util.DateUtils;
import com.haizhi.graph.common.util.Getter;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by chengmo on 2018/1/2.
 */
public class HashKeyGetter implements KeyGetter {

    private static final KeyHasher KEY_HASHER = KeyFactory.createKeyHasher();

    private String _hash;

    @Override
    public String getLongKey(String key) {
        if (!StringUtils.contains(key, "/")) {
            return String.valueOf(KEY_HASHER.toLong(key));
        }
        String[] arr = key.split("/");
        return arr[0] + "/" + KEY_HASHER.toLong(arr[1]);
    }

    @Override
    public String getRowKey(String objectKey) {
        this.initializePartitionHash(objectKey);
        return format("{0}#{1}", _hash, objectKey);
    }

    @Override
    public String getRowKey(String objectKey, String fromKey, String toKey) {
        this.initializePartitionHash(formatKey(fromKey));
        return format("{0}#{1}~{2}#{3}", _hash, fromKey, toKey, objectKey);
    }

    @Override
    public String getRowKey(String objectKey, String fromKey, String toKey, String createTime) {
        this.initializePartitionHash(formatKey(fromKey));
        return format("{0}#{1}~{2}#{3}#{4}", _hash, fromKey, toKey, createTime, objectKey);
    }

    @Override
    public String getSummaryRowKey(String fromKey, String toKey, String dateRange) {
        this.initializePartitionHash(formatKey(fromKey));
        String timeRange = KeyUtils.formatTimeRange(dateRange);
        return format("{0}#{1}~{2}#{3}", _hash, fromKey, toKey, timeRange);
    }

    @Override
    public String getRowKey(Map<String, String> data) {
        String fromKey = Getter.get(Keys.FROM_KEY, data);
        String toKey = Getter.get(Keys.TO_KEY, data);
        String objectKey = data.get(Keys.OBJECT_KEY);
        String rowKey = "";

        String ctime = Getter.get(Keys.CTIME, data);
        if (StringUtils.isNoneBlank(ctime)) {
            ctime = KeyUtils.formatTime(ctime);
            rowKey = this.getRowKey(objectKey, fromKey, toKey, ctime);
        } else {
            rowKey = this.getRowKey(objectKey, fromKey, toKey);
        }
        return rowKey;
    }

    @Override
    public String getHistoryRowKey(String objectKey) {
        String _hash = KEY_HASHER.getPartitionHash(objectKey);
        String date = StringUtils.substringBefore(DateUtils.toUTC(new Date()), ":");
        return format("{0}#{1}#{2}", _hash, date, objectKey);
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private void initializePartitionHash(String md5) {
        this._hash = KEY_HASHER.getPartitionHash(md5);
    }

    private String formatKey(String key) {
        return StringUtils.substringAfter(key, "/");
    }

    private String format(String pattern, Object... arguments) {
        return MessageFormat.format(pattern, arguments);
    }
}
