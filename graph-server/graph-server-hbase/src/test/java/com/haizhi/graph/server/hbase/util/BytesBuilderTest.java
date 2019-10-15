package com.haizhi.graph.server.hbase.util;

import com.haizhi.graph.server.api.bean.StoreURL;
import org.junit.Test;

/**
 * Created by tanghaiyang on 2019/7/8.
 */
public class BytesBuilderTest {

    @Test
    public void testSerialize() {
        double value = 1000000000000.0000;
        StoreURL storeURL = new StoreURL();
        storeURL.setUrl("dddd");
        storeURL.setUser("test");

        System.out.println(value);
        byte[] bytes = BytesBuilder.serialize(storeURL);
        Object oo = BytesBuilder.deserialize(bytes);

        System.out.println(oo);
    }
    
}
