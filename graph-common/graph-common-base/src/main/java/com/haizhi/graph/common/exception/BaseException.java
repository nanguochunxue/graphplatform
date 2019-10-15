package com.haizhi.graph.common.exception;

import java.text.MessageFormat;

/**
 * Created by chengmo on 2018/5/16.
 */

public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private int code = -1;
    private String desc;
    private Object[] args;

    public BaseException() {
    }

    public BaseException(String desc) {
        super(desc);
    }

    public BaseException(String descPattern, Object... args) {
        this(-1, descPattern, args);
    }

    public BaseException(Throwable source) {
        super(source);
    }

    public BaseException(String desc, Throwable source) {
        this(-1, desc, source);
    }

    public BaseException(String descPattern, Throwable source, Object... args) {
        this(-1, descPattern, source, args);
    }

    public BaseException(int code, String desc) {
        super("[" + code + "]" + desc);
        this.setErrorCode(code, desc);
    }

    public BaseException(int code, String descPattern, Object... args) {
        super("[" + code + "]" + format(descPattern, args));
        this.setErrorCode(code, descPattern, args);
    }

    public BaseException(int code, String desc, Throwable source) {
        super("[" + code + "]" + desc, source);
        this.setErrorCode(code, desc);
    }

    public BaseException(int code, String descPattern, Throwable source, Object... args) {
        super("[" + code + "]" + format(descPattern, args), source);
        this.setErrorCode(code, descPattern, args);
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        if (args == null){
            return desc;
        }
        return format(desc, args);
    }


    ///////////////////////
    // private functions
    ///////////////////////
    private void setErrorCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private void setErrorCode(int code, String desc, Object... args) {
        this.code = code;
        this.desc = desc;
        this.args = args;
    }

    private static String format(String pattern, Object... arguments) {
        return MessageFormat.format(pattern, arguments);
    }
}
