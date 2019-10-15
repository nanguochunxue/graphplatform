package com.haizhi.graph.sys.auth.shiro;

import com.haizhi.graph.common.redis.key.RKeys;
import com.haizhi.graph.sys.auth.shiro.filter.UserFilter;
import com.haizhi.graph.sys.auth.shiro.filter.UserRolesAuthorizationFilter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.IRedisManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisClusterManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSentinelManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by chengmo on 2018/1/4.
 */

@Configuration
public class ShiroConfig {

    //seconds
    @Value("${server.session.timeout:1800}")
    private int timeout;

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager());
        LinkedHashMap<String, Filter> filters = new LinkedHashMap<>();
        filters.put("cookie", cookieFilter());
        filters.put("roles", new UserRolesAuthorizationFilter());
        filters.put("user", new UserFilter());
        bean.setFilters(filters);
        bean.setLoginUrl("/auth/unauth");
        bean.setSuccessUrl("/index");
        bean.setUnauthorizedUrl("/auth/unauth");
        bean.setFilterChainDefinitionMap(chainDefinitionProvider().getAllRolesPermissions());
        return bean;
    }

    @Bean
    public SecurityManager securityManager() {
        //安全管理器
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm());
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(redisCacheManager());
        securityManager.setRememberMeManager(cookieRememberMeManager());
        return securityManager;
    }

    @Bean
    public ChainDefinitionProvider chainDefinitionProvider() {
        return new ChainDefinitionProvider();
    }

    @Bean
    public UserRealm userRealm() {
        return new UserRealm(new UserCredentialsMatcher());
    }

    @Bean
    public EhCacheManager ehCacheManager() {
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:ehcache.xml");
        return cacheManager;
    }

    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionDAO(redisSessionDAO());
        sessionManager.setCacheManager(redisCacheManager());
        // milliseconds
        sessionManager.setGlobalSessionTimeout(timeout * 1000);
        return sessionManager;
    }

    @Bean
    public CustomRedisSessionDAO redisSessionDAO() {
        CustomRedisSessionDAO redisSessionDAO = new CustomRedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        redisSessionDAO.setExpire(timeout);
        redisSessionDAO.setKeyPrefix(getUnifyPrefix(redisSessionDAO.getKeyPrefix()));
        redisSessionDAO.setSessionIdGenerator(getSessionIdGenerator());
        return redisSessionDAO;
    }

    private String getUnifyPrefix(String keyPrefix) {
        return RKeys.SSO_SESSION + keyPrefix;
    }

    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }

    @Bean
    public SessionIdGenerator getSessionIdGenerator(){
        return new CustomSessionIdGenerator();
    }

    @Bean
    public IRedisManager redisManager() {
        if (Objects.nonNull(redisProperties.getCluster()) && CollectionUtils.isNotEmpty(redisProperties.getCluster().getNodes())) {
            List<String> nodes = redisProperties.getCluster().getNodes();
            StringBuilder redisNodes = new StringBuilder();
            Iterator<String> iterator = nodes.iterator();
            while (iterator.hasNext()) {
                redisNodes.append(iterator.next());
                if (iterator.hasNext()) {
                    redisNodes.append(",");
                }
            }
            RedisClusterManager clusterManager = new RedisClusterManager();
            clusterManager.setHost(redisNodes.toString());
            clusterManager.setPassword(redisProperties.getPassword());
            clusterManager.setDatabase(redisProperties.getDatabase());
            clusterManager.setTimeout(redisProperties.getTimeout());
            return clusterManager;
        } else if (Objects.nonNull(redisProperties.getSentinel()) && StringUtils.isNotBlank(redisProperties.getSentinel().getMaster()) && StringUtils.isNotBlank(redisProperties.getSentinel().getNodes())) {
            RedisSentinelManager sentinelManager = new RedisSentinelManager();
            sentinelManager.setHost(redisProperties.getSentinel().getNodes());
            sentinelManager.setDatabase(redisProperties.getDatabase());
            sentinelManager.setMasterName(redisProperties.getSentinel().getMaster());
            sentinelManager.setPassword(redisProperties.getPassword());
            sentinelManager.setTimeout(redisProperties.getTimeout());
            return sentinelManager;
        } else {
            RedisManager redisManager = new RedisManager();
            redisManager.setHost(redisProperties.getHost() + ":" + redisProperties.getPort());
            redisManager.setDatabase(redisProperties.getDatabase());
            redisManager.setPassword(redisProperties.getPassword());
            redisManager.setTimeout(redisProperties.getTimeout());
            return redisManager;
        }
    }

    @Bean
    public FormAuthenticationFilter cookieFilter() {
        FormAuthenticationFilter filter = new FormAuthenticationFilter();
        filter.setRememberMeParam("haizhi-sso");
        return filter;
    }

    @Bean
    public SimpleCookie simpleCookie() {
        SimpleCookie cookie = new SimpleCookie();
        cookie.setHttpOnly(true);
        cookie.setMaxAge(Cookie.ONE_YEAR);
        cookie.setName("haizhi-sso");
        cookie.setPath("/");
        return cookie;
    }

    @Bean
    public CookieRememberMeManager cookieRememberMeManager() {
        CookieRememberMeManager manager = new CookieRememberMeManager();
        manager.setCookie(simpleCookie());
        return manager;
    }
}
