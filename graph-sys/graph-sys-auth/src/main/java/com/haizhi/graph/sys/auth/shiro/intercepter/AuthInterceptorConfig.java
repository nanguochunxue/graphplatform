package com.haizhi.graph.sys.auth.shiro.intercepter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Create by zhoumingbing on 2019-07-05
 */
@Configuration
public class AuthInterceptorConfig extends WebMvcConfigurerAdapter {

    @Bean
    public AuthorizationInterceptor authorizationInterceptor() {
        return new AuthorizationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(authorizationInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/**/swagger-ui.html",
                        "/**/swagger-resources/**",
                        "/**/api-docs/**",
                        "/**/enableLogAudit",
                        "/**/disableLogAudit"
                );
    }
}
