package com.haizhi.graph.server.api.exception;

import com.haizhi.graph.common.exception.BaseException;

/**
 * Created by chengmo on 2019/6/20.
 */
public class EsServerException extends BaseException {

    public EsServerException(String desc) {
        super(desc);
    }

    public EsServerException(Throwable throwable) {
        super(throwable);
    }
}
