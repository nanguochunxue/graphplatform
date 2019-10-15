package com.haizhi.graph.dc.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by chengangxiong on 2019/03/25
 */
@SpringBootApplication
@ServletComponentScan
@EnableScheduling
@EnableJpaAuditing
@EnableJpaRepositories({
        "com.haizhi.graph.dc.store",
        "com.haizhi.graph.dc.core",
        "com.haizhi.graph.sys",
})
@EntityScan({
        "com.haizhi.graph.dc.store",
        "com.haizhi.graph.dc.core",
        "com.haizhi.graph.sys",
})
@ComponentScan({
        "com.haizhi.graph.common",
        "com.haizhi.graph.server",
        "com.haizhi.graph.sys",
        "com.haizhi.graph.dc.core",
        "com.haizhi.graph.dc.store"
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
