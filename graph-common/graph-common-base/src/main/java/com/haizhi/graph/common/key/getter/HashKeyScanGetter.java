package com.haizhi.graph.common.key.getter;

import com.haizhi.graph.common.key.KeyFactory;
import com.haizhi.graph.common.key.KeyHasher;
import com.haizhi.graph.common.key.KeyScanGetter;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.util.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Date;

/**
 * Created by chengmo on 2018/1/2.
 */
public class HashKeyScanGetter implements KeyScanGetter{

    private static final KeyHasher KEY_HASHER = KeyFactory.createKeyHasher();

    @Override
    public String getStartKey(String fromKey, String toKey, String timestamp) {
        String _hash = KEY_HASHER.getPartitionHash(formatKey(fromKey));
        return format("{0}#{1}~{2}#{3}", _hash, fromKey, toKey, timestamp);
    }

    @Override
    public String getStopKey(String fromKey, String toKey, String timestamp) {
        String rowKey = getStartKey(fromKey, toKey, timestamp);
        // ~ 对应的ASCII码为126大于所有数字字母，所以作为StopRowKey的后缀
        return rowKey + Keys.KS3;
    }

    @Override
    public String getStartHistoryKey() {
        String utc = DateUtils.toUTC(DateUtils.getYearsBefore(new Date(), 3));
        String date = StringUtils.substringBefore(utc, ":");
        return format("000#{0}", date);
    }

    @Override
    public String getStopHistoryKey(int daysBefore) {
        daysBefore = daysBefore < 0 ? 0 : daysBefore;
        String utc = DateUtils.toUTC(DateUtils.getDaysBefore(new Date(), daysBefore));
        String date = StringUtils.substringBefore(utc, ":");
        return format("999#{0}~", date);
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private String formatKey(String key){
        return StringUtils.substringAfter(key, "/");
    }

    private String format(String pattern, Object... arguments) {
        return MessageFormat.format(pattern, arguments);
    }
}
