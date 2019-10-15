package com.haizhi.graph.sys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableJpaRepositories({"com.haizhi.graph.sys"})
@EntityScan({"com.haizhi.graph.sys"})
@ComponentScan({
        "com.haizhi.graph.common",
        "com.haizhi.graph.sys"
        })
public class ApplicationTest {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationTest.class, args);
    }
}
