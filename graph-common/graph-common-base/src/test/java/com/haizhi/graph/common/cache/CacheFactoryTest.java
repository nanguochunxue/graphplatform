package com.haizhi.graph.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by chengangxiong on 2019/05/10
 */
public class CacheFactoryTest {

    @Test
    public void test(){
        String a = "a/b";
        a = a.replaceAll("/", "aa");
        System.out.println(a);
    }

    @Test
    public void testCache() throws InterruptedException {
        Cache<String, Boolean> CACHE = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.SECONDS).build();

        CACHE.put("a", true);

        Thread.sleep(6 * 1000);
        System.out.print(CACHE.getIfPresent("a"));
    }
}