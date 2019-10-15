package com.haizhi.graph.tag.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by chengmo on 2018/1/4.
 */
@EnableCaching
@SpringBootApplication
@EnableJpaRepositories({"com.haizhi.graph.tag.core"})
@EntityScan({"com.haizhi.graph.tag.core"})
@ComponentScan({
        "com.haizhi.graph.common.context",
        "com.haizhi.graph.tag.core"})
public class ApplicationTest {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationTest.class, args);
    }
}
