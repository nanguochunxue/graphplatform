package com.haizhi.graph.common.key.partition;

import com.haizhi.graph.common.key.KeyUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengmo on 2017/12/14.
 */
public class RowKeyPartitioner {

    /**
     * Get partitions split keys.
     *
     * @param logicPartitions
     * @param physicsPartitions
     * @return
     */
    public static byte[][] getSplitKeysBytes(int logicPartitions, int physicsPartitions) {
        List<String> list = getSplitKeys(logicPartitions, physicsPartitions);
        byte[][] splitKeys = new byte[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            splitKeys[i] = list.get(i).getBytes(Charset.forName("UTF-8"));
        }
        return splitKeys;
    }

    /**
     * Get partitions split keys.
     *
     * @param logicPartitions
     * @param physicsPartitions
     * @return
     */
    public static List<String> getSplitKeys(int logicPartitions, int physicsPartitions) {
        if (logicPartitions <= 0 || physicsPartitions <= 0) {
            throw new IllegalArgumentException();
        }

        int interval = logicPartitions / physicsPartitions;
        int remainder = logicPartitions % physicsPartitions;

        List<String> result = new ArrayList<>();
        for (int i = 0; i < logicPartitions; i++) {
            if (i % interval == 0) {
                if (i == 0) {
                    continue;
                }
                result.add(KeyUtils.formatPlaceHolder(i == 0 ? 0 : i - 1, 3));
            }
        }
        if (result.size() == physicsPartitions) {
            result.remove(result.size() - 1);
        }
        return result;
    }
}
