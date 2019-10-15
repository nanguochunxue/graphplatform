package com.haizhi.graph.dc.inbound.vfs.exception;

import java.io.IOException;

/**
 * Created by chengangxiong on 2019/01/18
 */
public class DcFileException extends RuntimeException {
    public DcFileException(String msg) {
        super(msg);
    }

    public DcFileException(String msg, IOException e) {
        super(msg, e);
    }

    public DcFileException(Exception e) {
        super(e);
    }
}
