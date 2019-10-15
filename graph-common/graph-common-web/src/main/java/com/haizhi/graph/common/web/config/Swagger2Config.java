package com.haizhi.graph.common.web.config;

import com.google.common.base.Predicates;
import com.haizhi.graph.common.context.Module;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.text.MessageFormat;

/**
 * Created by chengmo on 2018/7/4.
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {
    private static final GLog LOG = LogFactory.getLogger(Swagger2Config.class);
    private static final String BASE_PACKAGE_TPL = "com.haizhi.{0}.controller";

    @Bean
    public Docket createRestApi() {
        String moduleName = Module.getName();
        String basePackage = MessageFormat.format(BASE_PACKAGE_TPL, moduleName.replaceAll("-", "."));
        LOG.info("base package: {0}", basePackage);
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder().title(moduleName + " Restful API").build())
                .select()
                .apis(Predicates.or(RequestHandlerSelectors.basePackage(basePackage),
                        RequestHandlerSelectors.basePackage("com.haizhi.graph.common.web.controller")))
                .paths(PathSelectors.any())
                .build();
    }
}
