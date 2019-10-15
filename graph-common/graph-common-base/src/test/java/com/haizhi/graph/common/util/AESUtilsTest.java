package com.haizhi.graph.common.util;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liulu on 2019/7/22.
 */
public class AESUtilsTest {


    @Test
    public void AESTest() {
        String key = "hzAtlasProdution";
        String content = "cjfpersonal";
        try {
            System.out.println("加密前：" + content);

            String encrypt = AESUtils.encrypt(content, key);
            System.out.println("加密后：" + encrypt);

            String decrypt = AESUtils.decrypt(encrypt, key);
            System.out.println("解密后：" + decrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
