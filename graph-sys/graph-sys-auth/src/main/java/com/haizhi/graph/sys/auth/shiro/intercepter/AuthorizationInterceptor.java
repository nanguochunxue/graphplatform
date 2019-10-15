package com.haizhi.graph.sys.auth.shiro.intercepter;

import com.google.common.collect.ImmutableList;
import com.haizhi.graph.common.constant.GraphStatus;
import com.haizhi.graph.common.core.login.model.UserSysRoleVo;
import com.haizhi.graph.common.core.login.utils.ShiroUtils;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.sys.auth.constant.SysType;
import com.haizhi.graph.sys.auth.dao.SysResourceDao;
import com.haizhi.graph.sys.auth.model.po.SysResourcePo;
import com.haizhi.graph.sys.auth.service.SysLoginUserCache;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Create by zhoumingbing on 2019-07-05
 */
@Order(0)
public class AuthorizationInterceptor implements HandlerInterceptor {
    private static final GLog LOG = LogFactory.getLogger(AuthorizationInterceptor.class);

    private Set<String> permissionURLList = new HashSet<>();

    @Autowired
    private SysResourceDao sysResourceDao;

    @Autowired
    private SysLoginUserCache sysLoginUserCache;

    @PostConstruct
    public void loadResourceURL() {
        List<String> typeList = ImmutableList.of(SysType.DMP.getCode(), SysType.SYS.getCode());
        List<SysResourcePo> sysResourcePoList = sysResourceDao.findByTypeIn(typeList);
        if (CollectionUtils.isEmpty(sysResourcePoList)) {
            return;
        }
        Set<String> resourceSets = sysResourcePoList.stream()
                .filter(po -> StringUtils.isNotBlank(po.getUrl()))
                .map(SysResourcePo::getUrl).collect(Collectors.toSet());
        permissionURLList.addAll(resourceSets);
        LOG.info("application start , load resource size={0}", permissionURLList.size());
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestUri = WebUtils.getRequestUri(request);
        if (!permissionURLList.contains(requestUri)) {
            return true;
        }
        UserSysRoleVo currentUser = ShiroUtils.getCurrentUser();
        if (Objects.isNull(currentUser)) {
            return false;
        }
        Set<String> permissions = sysLoginUserCache.getPermissions(currentUser.getId(), SysType.SYS, SysType.DMP);
        boolean allow = permissions.contains(requestUri);
        if (!allow) {
            LOG.info("not allow request url={0}", requestUri);
            com.haizhi.graph.sys.auth.shiro.util.WebUtils.responseJson(response, GraphStatus.FORBIDDEN);
        }
        return allow;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}