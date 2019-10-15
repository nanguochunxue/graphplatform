package com.haizhi.graph.sys.auth.shiro;

import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.core.login.model.RoleVo;
import com.haizhi.graph.common.core.login.model.UserSysRoleVo;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.sys.auth.service.SysRoleService;
import com.haizhi.graph.sys.auth.service.SysUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by chengmo on 2018/1/4.
 */
public class UserRealm extends AuthorizingRealm {

    private static final GLog LOG = LogFactory.getLogger(UserRealm.class);

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService roleService;

    public UserRealm(CredentialsMatcher matcher) {
        super(matcher);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        String userLoginInfo = (String) principals.getPrimaryPrincipal();
        UserSysRoleVo userRoleDto = JSONObject.parseObject(userLoginInfo, UserSysRoleVo.class);

        Set<String> roleSet = new HashSet<>();
        if (userRoleDto.getRoles() != null) {
            for (RoleVo role : userRoleDto.getRoles()) {
                roleSet.add(role.getId().toString());
            }
        }
        authorizationInfo.setRoles(roleSet);
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken credential = (UsernamePasswordToken) token;
        UserSysRoleVo user = sysUserService.findUserRole(credential.getUsername());
        if (Objects.isNull(user)) {
            LOG.error("Failed to get authenticationInfo of {0}", credential.getUsername());
            throw new AccountException();
        }
        String userLoginInfo = JSONObject.toJSONString(user);
        return new SimpleAuthenticationInfo(userLoginInfo, user.getPassword(), user.getUserNo());
    }
}
