package com.haizhi.graph.plugins.flume.source.file.core;

import com.haizhi.graph.common.util.DateUtils;

import java.io.File;

/**
 * Created by chengmo on 2018/11/30.
 */
public enum DataPartition {
    DAY,
    DAY_BEFORE,
    DAY$HOUR,
    DAY$HOUR_BEFORE;

    /**
     * Get path by partition.
     * Example:
     *      DAY             -> 2018-09-30
     *      DAY_BEFORE      -> 2018-09-29
     *      DAY$HOUR        -> 2018-09-30/09
     *      DAY$HOUR_BEFORE -> 2018-09-30/08
     *
     * @param partition
     * @return
     */
    public static String getPath(DataPartition partition){
        if (partition == null){
            return null;
        }
        switch (partition){
            case DAY:
                return DateUtils.getToday();
            case DAY_BEFORE:
                return DateUtils.getYesterday();
            case DAY$HOUR:
                return DateUtils.getTodayAndHour().replaceAll(" ", File.separator);
            case DAY$HOUR_BEFORE:
                return DateUtils.getTodayAndHourBefore().replaceAll(" ", File.separator);
        }
        return null;
    }
}
