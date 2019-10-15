package com.haizhi.graph.dc.inbound.vfs.access;

import java.io.IOException;

/**
 * Created by chengangxiong on 2019/01/28
 */
public interface ReadLine {

    String readLine() throws IOException;

    String firstLine() throws IOException;
}
