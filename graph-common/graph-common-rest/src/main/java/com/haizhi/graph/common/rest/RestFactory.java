package com.haizhi.graph.common.rest;

/**
 * Created by chengangxiong on 2018/12/18
 */
public class RestFactory {

    private static class SingletonHolder {
        private static final RestService INSTANCE = new RestServiceImpl(
                new RestClientConfig().restTemplate());
    }

    /**
     * Get rest service with restTemplate.
     * only when the spring ioc container is not used.
     *
     * @return
     */
    public static final RestService getRestService() {
        return SingletonHolder.INSTANCE;
    }
}
