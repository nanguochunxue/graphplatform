package com.haizhi.graph.search.restapi;

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
		"com.haizhi.graph.dc.core",
		"com.haizhi.graph.sys.file",
		"com.haizhi.graph.sys.core"
})
@EntityScan({
		"com.haizhi.graph.dc.core",
		"com.haizhi.graph.sys.file",
		"com.haizhi.graph.sys.core"
})
@ComponentScan({
		"com.haizhi.graph.common",
		"com.haizhi.graph.sys.file",
		"com.haizhi.graph.sys.core",
		"com.haizhi.graph.dc.core",
		"com.haizhi.graph.dc.store",
		"com.haizhi.graph.search.api",
		"com.haizhi.graph.search.restapi"
})
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
