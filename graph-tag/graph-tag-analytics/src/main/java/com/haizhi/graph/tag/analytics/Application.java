package com.haizhi.graph.tag.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by chengmo on 2018/1/4.
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableJpaRepositories({"com.haizhi.graph.tag.core","com.haizhi.graph.common.core"})
@EntityScan({"com.haizhi.graph.tag.core","com.haizhi.graph.common.core"})
@ComponentScan({
        "com.haizhi.graph.common.context",
        "com.haizhi.graph.common.core.base",
        "com.haizhi.graph.common.core.service.graph",
        "com.haizhi.graph.server.es",
        "com.haizhi.graph.server.hbase",
        "com.haizhi.graph.engine.base",
        "com.haizhi.graph.engine.flow",
        "com.haizhi.graph.tag.core",
        "com.haizhi.graph.tag.analytics"
        })
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
