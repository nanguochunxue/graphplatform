package com.haizhi.graph.common.key;

import com.haizhi.graph.common.util.DateUtils;

import java.text.NumberFormat;

/**
 * Created by chengmo on 2017/12/7.
 */
public class KeyUtils {

    /* Maximum logic partitions */
    public static final int LOGIC_PARTITIONS = 1000;

    /* Minimum Integer digits */
    public static final int HASH_DIGITS = 3;
    public static final int GRAPH_ID_DIGITS = 3;
    public static final int SCHEMA_ID_DIGITS = 4;

    private static NumberFormat formatter = NumberFormat.getNumberInstance();

    public static String formatPlaceHolder(long number, int digits){
        formatter.setMinimumIntegerDigits(digits);
        formatter.setGroupingUsed(false);
        return formatter.format(number);
    }

    public static String formatTime(String time){
        return String.valueOf(DateUtils.toLocalMillis(time));
    }

    public static String formatTimeRange(String timeRange){
        String result = "";
        if (timeRange.contains("|")){
            String[] ary = timeRange.split("\\|");
            result = formatTime(ary[0]) + Keys.KS3 + formatTime(ary[1]);
        }
        return result;
    }
}
