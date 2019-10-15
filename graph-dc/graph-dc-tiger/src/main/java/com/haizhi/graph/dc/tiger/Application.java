package com.haizhi.graph.dc.tiger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by chengmo on 2018/10/25.
 */
@SpringBootApplication
@ServletComponentScan
@EnableScheduling
@EnableJpaAuditing
@EnableJpaRepositories({"com.haizhi.graph.dc.core"})
@EntityScan({"com.haizhi.graph.dc.core"})
@ComponentScan({
        "com.haizhi.graph.common.context",
        "com.haizhi.graph.common.web",
        "com.haizhi.graph.common.rest",
        "com.haizhi.graph.server.kafka",
        "com.haizhi.graph.server.hbase",
        "com.haizhi.graph.server.api",
        "com.haizhi.graph.server.tiger",
        "com.haizhi.graph.dc.tiger",
        "com.haizhi.graph.dc.core",
        "com.haizhi.graph.dc.common"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
