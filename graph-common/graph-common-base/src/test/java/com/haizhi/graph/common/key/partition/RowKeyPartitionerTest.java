package com.haizhi.graph.common.key.partition;

import org.junit.Test;

import java.util.List;

/**
 * Created by chengmo on 2017/12/14.
 */
public class RowKeyPartitionerTest {

    @Test
    public void getSplitKeys(){
        List<String> keys = RowKeyPartitioner.getSplitKeys(1000, 16);
        System.out.println(keys);
        System.out.println(keys.size());
    }

    @Test
    public void getSplitKeysBytes(){
        byte[][] bytes = RowKeyPartitioner.getSplitKeysBytes(1000, 16);
        System.out.println(bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            System.out.println(new String(bytes[i]));
        }
    }
}
