package com.haizhi.graph.sys.auth.service;

import com.haizhi.graph.sys.auth.constant.SysType;
import com.haizhi.graph.sys.auth.model.vo.LoginUserVo;

import java.util.Set;

/**
 * Create by zhoumingbing on 2019-07-04
 */
public interface SysLoginUserCache {

    LoginUserVo getLoginUser(Long userId);

    Set<String> getPermissions(Long userId, SysType... sysTypes);

    void refresh(Long key);
}
