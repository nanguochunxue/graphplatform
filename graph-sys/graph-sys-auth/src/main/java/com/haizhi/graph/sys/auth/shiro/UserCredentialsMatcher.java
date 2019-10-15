package com.haizhi.graph.sys.auth.shiro;

import com.haizhi.graph.sys.auth.util.AuthUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;

/**
 * Created by chengmo on 2018/1/4.
 */
public class UserCredentialsMatcher implements CredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        UsernamePasswordToken pwdToken = (UsernamePasswordToken) token;
        return AuthUtils.validPassword((new String(pwdToken.getPassword())), (String) info.getCredentials());
    }
}
