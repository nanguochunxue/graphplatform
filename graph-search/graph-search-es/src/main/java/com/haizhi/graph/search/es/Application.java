package com.haizhi.graph.search.es;

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
@EnableJpaRepositories({
        "com.haizhi.graph.common.core",
        "com.haizhi.graph.search.es.dao",
        "com.haizhi.graph.dc.core",
        "com.haizhi.graph.dc.store",
        "com.haizhi.graph.sys.file"
})
@EntityScan({
        "com.haizhi.graph.common.core",
        "com.haizhi.graph.search.es.model",
        "com.haizhi.graph.dc.core",
        "com.haizhi.graph.dc.store",
        "com.haizhi.graph.sys.file"
})
@ComponentScan({
        "com.haizhi.graph.common",
        "com.haizhi.graph.server",
        "com.haizhi.graph.sys.file",
        "com.haizhi.graph.dc.store",
        "com.haizhi.graph.dc.core",
        "com.haizhi.graph.search.es"
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
