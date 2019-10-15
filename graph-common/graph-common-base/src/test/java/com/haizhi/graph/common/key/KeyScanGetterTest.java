package com.haizhi.graph.common.key;

import org.junit.Test;

/**
 * Created by chengmo on 2018/3/26.
 */
public class KeyScanGetterTest {

    @Test
    public void example(){
        KeyScanGetter scanGetter = KeyFactory.createKeyScanGetter();

        // history table scan
        System.out.println("startRow=" + scanGetter.getStartHistoryKey());
        System.out.println(" stopRow=" + scanGetter.getStopHistoryKey(7));
    }
}
