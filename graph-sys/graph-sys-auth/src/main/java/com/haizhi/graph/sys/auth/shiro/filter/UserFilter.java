package com.haizhi.graph.sys.auth.shiro.filter;

import com.haizhi.graph.common.constant.GraphStatus;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import static com.haizhi.graph.sys.auth.shiro.util.WebUtils.saveRequestAndResponseJson;

/**
 * Create by zhoumingbing on 2019-07-01
 */
public class UserFilter extends AuthenticationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        saveRequestAndResponseJson(request, response, GraphStatus.UNAUTHORIZED);
        return false;
    }
}
