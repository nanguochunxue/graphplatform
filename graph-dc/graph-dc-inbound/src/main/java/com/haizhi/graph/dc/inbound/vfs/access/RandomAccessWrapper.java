package com.haizhi.graph.dc.inbound.vfs.access;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by chengangxiong on 2019/01/21
 */
public interface RandomAccessWrapper extends Closeable {

    void seek(long pos) throws IOException;

    long getFilePointer() throws IOException;

    long length() throws IOException;

    int read(byte[] b) throws IOException;

    int read(byte b[], int off, int len) throws IOException;
}
