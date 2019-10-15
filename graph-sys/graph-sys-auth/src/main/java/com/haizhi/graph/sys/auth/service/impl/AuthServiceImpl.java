package com.haizhi.graph.sys.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.constant.Constants;
import com.haizhi.graph.common.constant.GraphStatus;
import com.haizhi.graph.common.core.jpa.JpaBase;
import com.haizhi.graph.common.core.login.model.UserSysRoleVo;
import com.haizhi.graph.common.core.login.utils.ShiroUtils;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.util.AESUtils;
import com.haizhi.graph.common.util.DateUtils;
import com.haizhi.graph.sys.auth.constant.AuthStatus;
import com.haizhi.graph.sys.auth.model.vo.LoginUserVo;
import com.haizhi.graph.sys.auth.model.vo.LoginVo;
import com.haizhi.graph.sys.auth.service.AuthService;
import com.haizhi.graph.sys.auth.service.SysLoginUserCache;
import com.haizhi.graph.sys.auth.shiro.model.LoginQo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Create by zhoumingbing on 2019-06-11
 */
@Service
public class AuthServiceImpl extends JpaBase implements AuthService {
    private static final GLog LOG = LogFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private SysLoginUserCache sysLoginUserCache;

    @Override
    public Response<LoginVo> login(LoginQo qo) {
        String userNo = qo.getUserNo();
        String password;
        try {
            password = AESUtils.decrypt(qo.getPassword());
        } catch (Exception e) {
            return Response.error(AuthStatus.INVALID_PASSWORD_ERROR);
        }
        if (StringUtils.isEmpty(userNo) || StringUtils.isEmpty(password)) {
            return Response.error(AuthStatus.USER_NAME_OR_PASSWORD_ERROR);
        }
        boolean rememberMe = Constants.Y.equalsIgnoreCase(qo.getAutoLogin());
        try {
            Subject currentUser = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(userNo, password);
            token.setRememberMe(rememberMe);
            currentUser.login(token);
            UserSysRoleVo user = JSON.parseObject((String) currentUser.getPrincipal(), UserSysRoleVo.class);
            currentUser.getSession().setAttribute("userID", user.getId());
            currentUser.getSession().setTimeout(rememberMe ? DateUtils.ONE_YEAR_MS : DateUtils.HALF_HOUR_MS);
            String sessionId = currentUser.getSession().getId().toString();
            String log = "login user:%s | remember me:%s | timeout:%s | sessionId:%s";
            LOG.info(String.format(log, userNo, rememberMe, currentUser.getSession().getTimeout(), sessionId));
            LoginVo loginVo = new LoginVo();
            loginVo.setSessionId(sessionId);
            return Response.success(loginVo);
        } catch (AuthenticationException e) {
            return Response.error(AuthStatus.USER_NAME_OR_PASSWORD_ERROR);
        } catch (Exception e) {
            LOG.error(e);
            return Response.error(AuthStatus.USER_NAME_OR_PASSWORD_ERROR);
        }
    }

    @Override
    public Response logout() {
        try {
            Subject currentUser = SecurityUtils.getSubject();
            if (currentUser != null && currentUser.getPrincipal() != null) {
                currentUser.logout();
                LOG.info("current user [{0}] logout", currentUser.getPrincipal());
            }
            return Response.success();
        } catch (Exception e) {
            LOG.error("logout error", e);
            return Response.error(AuthStatus.USER_LOGOUT_FAIL);
        }
    }

    @Override
    public Response<LoginUserVo> findLoginUser() {
        try {
            UserSysRoleVo currentUser = ShiroUtils.getCurrentUser();
            if (Objects.isNull(currentUser)){
                LOG.error("There is no shiro login user,currentUser is null");
                return Response.error(GraphStatus.UNAUTHORIZED);
            }
            LoginUserVo loginUserVo = sysLoginUserCache.getLoginUser(currentUser.getId());
            return Response.success(loginUserVo);
        } catch (Exception e) {
            LOG.error("no login user want to get login user info ", e);
            return Response.error(GraphStatus.UNAUTHORIZED);
        }
    }

    @Override
    public Response refreshUser(Long userId) {
        sysLoginUserCache.refresh(userId);
        return Response.success();
    }

    @Override
    public Response isLogin() {
        try {
            Subject subject = SecurityUtils.getSubject();
            boolean isLogin = Objects.nonNull(subject.getPrincipal());
            if (isLogin) {
                return Response.success();
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return Response.error();
    }
}
