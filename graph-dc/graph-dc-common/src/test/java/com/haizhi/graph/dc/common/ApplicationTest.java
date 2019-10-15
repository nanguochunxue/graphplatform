package com.haizhi.graph.dc.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by chengmo on 2018/05/14.
 */
@EnableJpaRepositories({"com.haizhi.graph.dc.core", "com.haizhi.graph.sys"})
@EntityScan({"com.haizhi.graph.common.core", "com.haizhi.graph.sys", "com.haizhi.graph.dc.core.model"})
@SpringBootApplication
@ComponentScan({
        "com.haizhi.graph.common.context",
        "com.haizhi.graph.common.core",
        "com.haizhi.graph.server.hbase",
        "com.haizhi.graph.dc.core",
        "com.haizhi.graph.sys.file",
        "com.haizhi.graph.dc.common"})
public class ApplicationTest {

    public static void main(String[] args) {
        SpringApplication.run(new Class[]{ApplicationTest.class}, args);
    }
}
