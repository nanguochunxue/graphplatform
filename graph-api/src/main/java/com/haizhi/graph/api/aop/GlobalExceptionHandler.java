package com.haizhi.graph.api.aop;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.v0.Response;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by yangyijun on 2018/6/8.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final GLog LOG = LogFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleException(HttpServletRequest request, IllegalStateException exception) throws Exception {
        return Response.error(exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleException(HttpServletRequest request, IllegalArgumentException exception) throws Exception {
        return Response.error(exception.getMessage());
    }

    //BindException
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleException(HttpServletRequest request, BindException exception) throws Exception {
        return Response.error(buildMessage(exception.getFieldErrors()));
    }

    //@Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleException(HttpServletRequest request, MethodArgumentNotValidException exception) throws Exception {
        return Response.error(buildMessage(exception.getBindingResult().getFieldErrors()));
    }

    //@RequestParam
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleException(HttpServletRequest request, MissingServletRequestParameterException exception) throws Exception {
        StringBuffer sb = new StringBuffer();
        buildLog(sb, exception.getParameterName(), exception.getMessage());
        return Response.error(sb.toString());
    }

    @ExceptionHandler(UnexpectedStatusException.class)
    public Response handleException(HttpServletRequest request, HttpServletResponse response,
                                    UnexpectedStatusException ex) throws Exception {
        recordErrorLog(ex, request, ex);
        return Response.error(ex);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleException(HttpServletRequest request, Exception exception) throws Exception {
        return Response.error(exception.getMessage());
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private void recordErrorLog(UnexpectedStatusException ex, HttpServletRequest request, Throwable throwable) throws IOException {
        HttpRequestInfo info = new HttpRequestInfo();
        info.setPath(request.getServletPath());
        info.setParameters(request.getParameterMap());
        //todo 此处代码可能读取了request已关闭的流，导致二次异常，暂时屏蔽
        //info.setBody(request.getReader().lines().reduce((s1, s2) -> s1 + s2).orElse(""));
        LOG.error("ErrorCode: {0}\nRequest>>>{1}", throwable, ex.getMessage(), JSON.toJSONString(info));
    }

    private void buildLog(StringBuffer sb, String field, String msg) {
        sb.append("field:[");
        sb.append(field);
        sb.append("] message:[");
        sb.append(msg);
        sb.append("]");
        LOG.error(sb.toString());
    }

    private String buildMessage(List<FieldError> fieldErrors) throws IOException {
        StringBuffer sb = new StringBuffer();
        for (FieldError fieldError : fieldErrors) {
            buildLog(sb, fieldError.getField(), fieldError.getDefaultMessage());
        }
        return sb.toString();
    }

    @Data
    public static class HttpRequestInfo {
        private String path;
        private Map<String, String[]> parameters;
        private String body;
    }
}
