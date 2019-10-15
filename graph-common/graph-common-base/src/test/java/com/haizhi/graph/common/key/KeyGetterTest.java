package com.haizhi.graph.common.key;

import org.junit.Test;

/**
 * Created by chengmo on 2018/1/2.
 */
public class KeyGetterTest {

    @Test
    public void getLongKey(){
        KeyGetter keyGetter = KeyFactory.createKeyGetter();

        String objectKey = "eff1213f8a5d883a86f5d6b5fbe20e3c";
        String fromKey = "Company/79639cb8b90edfe0b429502697d98bd8";
        String toKey = "Person/6375c3b7421d28dd51647cd126c0747c";
        System.out.println(keyGetter.getLongKey(objectKey));
        System.out.println(keyGetter.getLongKey(fromKey));
        System.out.println(keyGetter.getLongKey(toKey));
    }

    @Test
    public void getRowKey(){
        KeyGetter keyGetter = KeyFactory.createKeyGetter();

        String objectKey = "EFF1213F8A5D883A86F5D6B5FBE20E3C";
        String fromKey = "Company/79639CB8B90EDFE0B429502697D98BD8";
        String toKey = "Person/6375C3B7421D28DD51647CD126C0747C";
        String createTime = "1510675200";
        String dateRange = "2017-11-15|2017-11-17";

        // 实体
        System.out.println("实体");
        System.out.println(keyGetter.getRowKey(objectKey));

        // 普通边
        System.out.println("普通边");
        System.out.println(keyGetter.getRowKey(objectKey, fromKey, toKey));

        // 统计边
        System.out.println("统计边");
        System.out.println(keyGetter.getRowKey(objectKey, fromKey, toKey, createTime));

        // 统计结果边
        System.out.println("统计结果边");
        System.out.println(keyGetter.getSummaryRowKey(fromKey, toKey, dateRange));

        // 历史表
        System.out.println("历史表");
        System.out.println(keyGetter.getHistoryRowKey(objectKey));
    }
}
