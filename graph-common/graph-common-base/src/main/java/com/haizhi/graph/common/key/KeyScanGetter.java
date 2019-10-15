package com.haizhi.graph.common.key;

/**
 * Created by chengmo on 2018/1/2.
 */
public interface KeyScanGetter {

    String getStartKey(String fromKey, String toKey, String timestamp);

    String getStopKey(String fromKey, String toKey, String timestamp);

    /**
     * Gets history table scan startRow.
     *
     * @return      ->Example: min(_hash) + ${currentDate - 3years}
     *                  000#2015-03-26T10
     */
    String getStartHistoryKey();

    /**
     * Gets history table scan stopRow.
     *
     * @param daysBefore    ->Example: 7 (now=2018-03-26T10:54:07+0800)
     * @return      ->Example: max(_hash) + ${currentDate - daysBefore} + ~
     *                  476#2018-03-19T10~
     */
    String getStopHistoryKey(int daysBefore);
}
