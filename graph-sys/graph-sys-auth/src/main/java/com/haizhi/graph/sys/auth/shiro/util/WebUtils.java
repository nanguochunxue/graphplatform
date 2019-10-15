package com.haizhi.graph.sys.auth.shiro.util;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.constant.Status;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

import static org.apache.shiro.web.util.WebUtils.saveRequest;

/**
 * Create by zhoumingbing on 2019-07-01
 */
public class WebUtils {
    private static final GLog LOG = LogFactory.getLogger(WebUtils.class);

    public static final String SESSION_ID_KEY  = "JSESSIONID";

    public static void saveRequestAndResponseJson(ServletRequest request, ServletResponse response, Status status) {
        saveRequest(request);
        responseJson(response, status);
    }

    public static void responseJson(ServletResponse servletResponse, Status status) {
        out((HttpServletResponse) servletResponse, Response.error(status));
    }

    public static void out(HttpServletResponse servletResponse, Response response) {
        PrintWriter out = null;
        try {
            servletResponse.setCharacterEncoding("UTF-8");
            servletResponse.setContentType("application/json; charset=utf-8");
            out = servletResponse.getWriter();
            out.write(JSON.toJSONString(response));
        } catch (IOException e) {
            LOG.error("response error,content={0}", JSON.toJSONString(response), e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static String getCurrentRequestSessionId(){
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        if (Objects.isNull(requestAttributes) || (!(requestAttributes instanceof ServletRequestAttributes))) {
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        if (Objects.isNull(request.getCookies())) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (SESSION_ID_KEY.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void setCurrentRequestSessionId(String sessionId){
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        if (Objects.isNull(requestAttributes) || (!(requestAttributes instanceof ServletRequestAttributes))) {
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        if (Objects.isNull(request.getCookies())) {
            return;
        }
        for (Cookie cookie : request.getCookies()) {
            if (SESSION_ID_KEY.equals(cookie.getName())) {
                cookie.setValue(sessionId);
            }
        }
    }
}
