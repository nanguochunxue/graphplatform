package com.haizhi.graph.common.redis;

import java.util.Set;

/**
 * Created by chengmo on 2018/7/2.
 */
public interface RedisService {

    Set<String> keys(String pattern);

    boolean hasKey(String key);

    <T> T get(String key);

    <T> boolean put(String key, T value);

    <T> boolean put(String key, T value, int timeout);

    boolean delete(String key);

    boolean deleteByPattern(String pattern);

    boolean publish(String channel, Object message);
}
