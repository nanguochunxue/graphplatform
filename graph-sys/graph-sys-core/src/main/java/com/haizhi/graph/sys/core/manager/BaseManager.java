package com.haizhi.graph.sys.core.manager;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.rest.RestService;
import com.haizhi.graph.sys.core.config.service.SysConfigService;
import com.haizhi.graph.sys.core.constant.SysCoreStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;

import java.net.URL;

/**
 * Created by chengmo on 2019/4/3.
 */
@Component
public class BaseManager {

    @Autowired
    private SysConfigService sysUrlService;
    @Autowired
    protected RestService restService;

    protected String getUrl(String key, String path) {
        return sysUrlService.getUrl(key) + path;
    }

    protected <Req, Resp> Resp doGet(String url, Req req, ParameterizedTypeReference<Resp> respType) {
        try {
            return restService.doGet(url, req, respType);
        } catch (Exception e) {
            return handleError(e, respType, url);
        }
    }

    protected <Req, Resp> Resp doPost(String url, Req req, ParameterizedTypeReference<Resp> respType) {
        try {
            return restService.doPost(url, req, respType);
        } catch (Exception e) {
            return handleError(e, respType, url);
        }
    }

    protected <Req, Resp> Resp doPostAsync(String url, Req req, ParameterizedTypeReference<Resp> respType) {
        try {
            return restService.doPostAsync(url, req, respType);
        } catch (Exception e) {
            return handleError(e, respType, url);
        }
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private <Resp> Resp handleError(Exception e, ParameterizedTypeReference<Resp> respType, String url) {
        if (e instanceof RestClientResponseException) {
            String body = RestClientResponseException.class.cast(e).getResponseBodyAsString();
            if (StringUtils.isEmpty(body)) {
                throw new UnexpectedStatusException(SysCoreStatus.CALL_REST_ERROR, e, getPath(url));
            } else {
                return JSON.parseObject(body, respType.getType());
            }
        } else {
            throw new UnexpectedStatusException(SysCoreStatus.CALL_REST_ERROR, e, getPath(url));
        }
    }

    private String getPath(String url) {
        String path;
        try {
            path = new URL(url).getPath();
        } catch (Exception e) {
            throw new UnexpectedStatusException(SysCoreStatus.URL_INVALID_ERROR, e, url);
        }
        return path;
    }
}
