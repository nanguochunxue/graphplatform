package com.haizhi.graph.server.api.exception;

import com.haizhi.graph.common.exception.BaseException;

/**
 * Created by chengmo on 2019/6/20.
 */
public class HBaseServerException extends BaseException {

    public HBaseServerException(Throwable throwable) {
        super(throwable);
    }
}
