package com.haizhi.graph.common.redis;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by liulu on 2019/4/28.
 */
@Service
public class RedisServiceImpl implements RedisService {

    private static final GLog LOG = LogFactory.getLogger(RedisServiceImpl.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            LOG.error(e);
        }
        return Collections.emptySet();
    }

    @Override
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    @Override
    public <T> T get(String key) {
        Object result;
        try {
            result = redisTemplate.opsForValue().get(key);
            if (null == result) {
                return null;
            }
            return (T) result;
        } catch (Exception e) {
            LOG.error("Get [Key:{0}] from redis failed!", e, key);
        }
        return null;
    }

    @Override
    public <T> boolean put(String key, T value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            LOG.error("Set [Key:{0}][Value:{1}] into redis failed!", e, key, value);

        }
        return false;
    }

    @Override
    public <T> boolean put(String key, T value, int timeout) {
        try {
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            LOG.error("Set [Key:{0}][Value:{1}][TimeOut:{2}] into redis failed!", e, key, value, timeout);
        }
        return false;
    }

    @Override
    public boolean delete(String key) {
        try {
            if (this.hasKey(key)) {
                redisTemplate.delete(key);
            }
            return true;
        } catch (Exception e) {
            LOG.error("Delete [Key:{0}] from redis failed!", e, key);
        }
        return false;
    }

    @Override
    public boolean deleteByPattern(String pattern){
        try {

            Set<String> keys = this.keys(pattern);
            if (!CollectionUtils.isEmpty(keys)){
                redisTemplate.delete(keys);
            }
            return true;
        } catch (Exception e) {
            LOG.error("Delete by pattern:{0} from redis failed!",e,pattern);
        }
        return false;
    }

    @Override
    public boolean publish(String channel, Object message) {
        try {
            redisTemplate.convertAndSend(channel, message);
            LOG.info("Success to publish [channel:{0}][message:{1}]!", channel, message);
            return true;
        } catch (Exception e) {
            LOG.error("Redis publish [channel:{0}][message:{1}] failed!", e, channel, message);
        }
        return false;
    }
}
