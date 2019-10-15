package com.haizhi.graph.sys.auth.service.impl;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.haizhi.graph.common.cache.CacheFactory;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.sys.auth.constant.SysType;
import com.haizhi.graph.sys.auth.model.vo.LoginUserVo;
import com.haizhi.graph.sys.auth.model.vo.SysResourceVo;
import com.haizhi.graph.sys.auth.service.SysLoginUserCache;
import com.haizhi.graph.sys.auth.service.SysUserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Create by zhoumingbing on 2019-07-04
 */
@Service
public class SysLoginUserCacheImpl implements SysLoginUserCache {

    private static final GLog LOG = LogFactory.getLogger(SysLoginUserCacheImpl.class);

    @Autowired
    private SysUserService sysUserService;

    private LoadingCache<Long, LoginUserVo> loginUserCache;

    public SysLoginUserCacheImpl() {
        this.loginUserCache = CacheFactory.create(new CacheLoader<Long, LoginUserVo>() {
            @Override
            public LoginUserVo load(Long key) throws Exception {
                return doLoad(Long.valueOf(key));
            }
        }, 10);
    }

    @Override
    public LoginUserVo getLoginUser(Long userId) {
        try {
            return loginUserCache.get(userId);
        } catch (ExecutionException e) {
            LOG.error(e);
        }
        return null;
    }

    @Override
    public Set<String> getPermissions(Long userId, SysType... sysTypes) {
        Set<String> permissions = Sets.newHashSet();
        try {
            LoginUserVo loginUser = getLoginUser(userId);
            Map<String, List<SysResourceVo>> resourceTrees = loginUser.getResourceTrees();
            if (resourceTrees == null) {
                return permissions;
            }
            for (SysType sysType : sysTypes) {
                List<SysResourceVo> sysResourceVos = resourceTrees.get(sysType.getCode());
                if (CollectionUtils.isEmpty(sysResourceVos)) {
                    continue;
                }
                permissions.addAll(ergodicList(sysResourceVos, Lists.newArrayList()));
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return permissions;
    }

    @Override
    public void refresh(Long key) {
        try {
            loginUserCache.refresh(key);
            LOG.info("Success to refresh LoginUser by key={0}", String.valueOf(key));
        } catch (Exception e) {
            throw e;
        }
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private LoginUserVo doLoad(Long userId) {
        return sysUserService.getLoginUserVo(userId);
    }

    private List<String> ergodicList(List<SysResourceVo> root, List<String> permissions) {
        for (int i = 0; i < root.size(); i++) {
            permissions.add(root.get(i).getUrl());
            if (null != root.get(i).getChildren()) {
                List<SysResourceVo> children = root.get(i).getChildren();
                ergodicList(children, permissions);
            }
        }
        return permissions;
    }
}
