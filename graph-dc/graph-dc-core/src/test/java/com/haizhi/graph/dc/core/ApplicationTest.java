package com.haizhi.graph.dc.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by chengmo on 2017/12/26.
 */
@EnableJpaRepositories({
        "com.haizhi.graph.dc.core",
        "com.haizhi.graph.sys"})
@EntityScan({
        "com.haizhi.graph.common.core",
        "com.haizhi.graph.dc.core.model",
        "com.haizhi.graph.sys"})
@ComponentScan({
        "com.haizhi.graph.common.context",
        "com.haizhi.graph.common.core",
        "com.haizhi.graph.common.redis",
        "com.haizhi.graph.dc.core",
        "com.haizhi.graph.sys"})
@SpringBootApplication
//@EnableCaching
public class ApplicationTest {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationTest.class, args);
    }
}
