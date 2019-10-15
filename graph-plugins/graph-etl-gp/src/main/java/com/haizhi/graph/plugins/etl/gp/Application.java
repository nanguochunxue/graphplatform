package com.haizhi.graph.plugins.etl.gp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by chengmo on 2019/03/25
 */
@SpringBootApplication
@ServletComponentScan
@ComponentScan({
        "com.haizhi.graph.common",
        "com.haizhi.graph.plugins.etl.gp"
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
