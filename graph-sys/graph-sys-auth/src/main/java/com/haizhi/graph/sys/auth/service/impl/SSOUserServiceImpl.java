package com.haizhi.graph.sys.auth.service.impl;

import com.haizhi.graph.common.constant.Constants;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.rest.RestService;
import com.haizhi.graph.common.util.AESUtils;
import com.haizhi.graph.sys.auth.constant.AuthStatus;
import com.haizhi.graph.sys.auth.service.SysUserService;
import com.haizhi.graph.sys.auth.shiro.model.LoginQo;
import com.haizhi.graph.sys.auth.shiro.util.WebUtils;
import com.haizhi.graph.sys.core.config.service.SysConfigService;
import com.haizhi.sys.sso.common.model.SSOLoginUser;
import com.haizhi.sys.sso.common.service.SSOUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Created by liulu on 2019/7/3.
 */
@Service
public class SSOUserServiceImpl implements SSOUserService {

    private static final GLog LOG = LogFactory.getLogger(SSOUserServiceImpl.class);

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RestService restService;

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private SysConfigService sysConfigService;

    @Override
    public Set<String> findWhiteUrls() {
        return sysConfigService.findShiroWhiteUrls();
    }

    @Override
    public void register(SSOLoginUser ssoLoginUser) throws Exception {
        sysUserService.register(ssoLoginUser);
        this.shiroLogin(ssoLoginUser);
    }

    private void shiroLogin(SSOLoginUser ssoLoginUser) throws Exception {
        String newSessionId = WebUtils.getCurrentRequestSessionId();
        if (StringUtils.isBlank(newSessionId)){
            newSessionId = UUID.randomUUID().toString();
            LOG.info("==== new randomUUID as sessionId for shiro Login:===" + newSessionId);
        }
        Map<String,String> cookie = new HashMap<>(2);
        cookie.put("Cookie",WebUtils.SESSION_ID_KEY  + "=" + newSessionId);
        LOG.info("==== get sessionId for shiro Login:===" + newSessionId);
        LoginQo loginQo = new LoginQo();
        loginQo.setAutoLogin(Constants.Y);
        loginQo.setUserNo(ssoLoginUser.getUserNo());
        String loginPwd = AESUtils.encrypt(com.haizhi.graph.common.core.login.constant.Constants.DEFAULT_USER_PWD);
        loginQo.setPassword(loginPwd);
        String url = String.format("http://localhost:%s%s%s", serverProperties.getPort(), serverProperties.getContextPath(),
                com.haizhi.graph.common.core.login.constant.Constants.LOGIN_PATH);
        Response response  = restService.doPost(url,loginQo, new ParameterizedTypeReference<Response>(){},cookie);
        if (Objects.isNull(response) || (!response.isSuccess())){
            String errMsg = AuthStatus.SHIRO_SSO_LOGIN_FAIL.getDesc();
            if (Objects.nonNull(response)){
                errMsg += response.getMessage();
            }
            throw new UnexpectedStatusException(AuthStatus.SHIRO_SSO_LOGIN_FAIL,errMsg);
        }
    }



}