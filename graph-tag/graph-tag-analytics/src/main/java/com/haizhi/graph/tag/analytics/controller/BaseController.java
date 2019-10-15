package com.haizhi.graph.tag.analytics.controller;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by chengmo on 2018/4/25.
 */
@Controller
public class BaseController {

    private static final GLog LOG = LogFactory.getLogger(BaseController.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    @ResponseBody
    public Response resolveException(HttpServletRequest request, Exception ex) {
        String message;
        if (ex instanceof HttpMessageNotReadableException) {
            message = "Json format error.";
        } else {
            message = "Missing system error.";
        }
        LOG.error(message, ex);
        return Response.error(message);
    }
}
