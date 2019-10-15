package com.haizhi.graph.dc.inbound.vfs.reader;

import com.haizhi.graph.dc.inbound.vfs.DcFileObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengangxiong on 2019/01/28
 */
public abstract class AbstractDataReader implements DcDataReader {

    protected DcFileObject dcFileObject;

    protected Map<String, Object> header = new HashMap<>();

    public AbstractDataReader(DcFileObject dcFileObject) {
        this.dcFileObject = dcFileObject;
        init();
    }

    protected void init() {
        header.put("file", dcFileObject.getPath());
    }

    protected Map<String, Object> getHeaders() {
        return header;
    }
}
