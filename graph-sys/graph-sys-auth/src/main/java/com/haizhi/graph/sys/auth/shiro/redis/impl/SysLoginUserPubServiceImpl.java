package com.haizhi.graph.sys.auth.shiro.redis.impl;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.redis.RedisService;
import com.haizhi.graph.common.redis.channel.ChannelKeys;
import com.haizhi.graph.sys.auth.service.SysUserService;
import com.haizhi.graph.sys.auth.shiro.redis.SysLoginUserPubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Create by zhoumingbing on 2019-07-05
 */
@Service
public class SysLoginUserPubServiceImpl implements SysLoginUserPubService {
    private static final GLog LOG = LogFactory.getLogger(SysLoginUserPubServiceImpl.class);

    @Autowired
    private RedisService redisService;

    @Autowired
    private SysUserService sysUserService;

    @Override
    public boolean publishByUserId(Long userId) {
        try {
            String message = String.valueOf(userId);
            LOG.info("Public sys_login_user by userId={0}", message);
            return redisService.publish(ChannelKeys.SYS_LOGIN_USER, message);
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    @Override
    public boolean publishByRoleId(Long roleId) {
        try {
            LOG.info("Public sys_login_user by roleId={0}", roleId);
            List<Long> userIdList = sysUserService.getUserIdByRoleId(roleId);
            Set<Long> unionUserIds = new HashSet<>(userIdList);
            unionUserIds.forEach(this::publishByUserId);
            return true;
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    @Override
    public boolean publishByUserIds(List<Long> userIds) {
        try {
            if (CollectionUtils.isEmpty(userIds)) {
                return false;
            }
            Set<Long> unionUserIds = new HashSet<>(userIds);
            unionUserIds.forEach(this::publishByUserId);
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }
}
