package com.haizhi.graph.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
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
@EnableCaching
@EnableJpaRepositories({
        "com.haizhi.graph.sys.core.config",
        "com.haizhi.graph.sys.auth",
        "com.haizhi.graph.sys.file",
        "com.haizhi.graph.dc.core",
        "com.haizhi.graph.dc.inbound",
        "com.haizhi.graph.search.style",
        "com.haizhi.graph.sys.core.config"
})
@EntityScan({
        "com.haizhi.graph.sys.core.config",
        "com.haizhi.graph.sys.auth",
        "com.haizhi.graph.sys.file",
        "com.haizhi.graph.dc.core",
        "com.haizhi.graph.dc.inbound",
        "com.haizhi.graph.search.style",
        "com.haizhi.graph.sys.core.config"
})
@ComponentScan({
        "com.haizhi.graph.common",
        "com.haizhi.graph.server",
        "com.haizhi.sys.sso",
        "com.haizhi.graph.sys.auth",
        "com.haizhi.graph.sys.file",
        "com.haizhi.graph.sys.core.config",
        "com.haizhi.graph.dc.core",
        "com.haizhi.graph.dc.store.api",
        "com.haizhi.graph.dc.common",
        "com.haizhi.graph.dc.inbound",
        "com.haizhi.graph.search.style",
        "com.haizhi.graph.api"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}



