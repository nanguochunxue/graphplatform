package com.haizhi.graph.dc.common.service.impl;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.redis.key.RKeys;
import com.haizhi.graph.dc.core.constant.DcConstants;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.common.service.TaskRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Create by zhoumingbing on 2019-06-05
 */
@Service
public class TaskRedisServiceImpl implements TaskRedisService {
    private static final GLog LOG = LogFactory.getLogger(TaskRedisServiceImpl.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean overErrorMode(DcInboundDataSuo suo) {
        try {
            Long taskInstanceId = suo.getHeaderOptions().getLong(DcConstants.KEY_TASK_INSTANCE_ID);
            if (taskInstanceId == null) {
                return false;
            }
            String key = RKeys.DC_TASK_GRAPH_ERROR_MODE + ":" + taskInstanceId;
            Object errorMode = redisTemplate.opsForValue().get(key);
            if (Objects.isNull(errorMode)) {
                return false;
            }
            return overErrorMode(taskInstanceId, (Integer) errorMode);
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    @Override
    public boolean overErrorMode(Long taskInstanceId, Integer errorMode) {
        try {
            if (errorMode == null || errorMode < 0 || taskInstanceId < 0) {
                return false;
            }
            return errorMode.compareTo(getErrorCount(taskInstanceId)) <= 0;
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    @Override
    public Integer getErrorCount(Long taskInstanceId) {
        Object count = redisTemplate.opsForValue().get(getErrorCountKey(taskInstanceId));
        if (count == null) {
            return 0;
        }
        return (Integer) count;
    }

    @Override
    public Long incrementErrorCount(Long taskInstanceId, long delta) {
        try {
            return redisTemplate.opsForValue().increment(getErrorCountKey(taskInstanceId), delta);
        } catch (Exception e) {
            LOG.error(e);
        }
        return Long.valueOf(getErrorCount(taskInstanceId));
    }

    @Override
    public boolean setErrorMode(Long taskInstanceId, Integer errorMode) {
        try {
            if (Objects.isNull(taskInstanceId) || Objects.isNull(errorMode)) {
                return false;
            }
            redisTemplate.opsForValue().set(getErrorModeKey(taskInstanceId), errorMode);
            return true;
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    private Object getErrorCountKey(Long taskInstanceId) {
        return RKeys.DC_ERROR_COUNT_KEY + ":" + taskInstanceId;
    }

    private Object getErrorModeKey(Long taskInstanceId) {
        return RKeys.DC_TASK_GRAPH_ERROR_MODE + ":" + taskInstanceId;
    }


}
