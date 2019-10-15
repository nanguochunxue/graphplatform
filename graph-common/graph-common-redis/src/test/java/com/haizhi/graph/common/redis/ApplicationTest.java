package com.haizhi.graph.common.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by chengmo on 2018/10/25.
 */
@SpringBootApplication
@EnableCaching
@ComponentScan({
        "com.haizhi.graph.common.context",
        "com.haizhi.graph.common.redis",})
public class ApplicationTest {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationTest.class, args);
    }
}
