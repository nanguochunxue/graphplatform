package com.haizhi.graph.sys.auth.shiro.redis;

import java.util.List;

/**
 * Create by zhoumingbing on 2019-07-05
 */
public interface SysLoginUserPubService {

    boolean publishByUserId(Long userId);

    boolean publishByRoleId(Long roleId);

    boolean publishByUserIds(List<Long> userIds);
}
