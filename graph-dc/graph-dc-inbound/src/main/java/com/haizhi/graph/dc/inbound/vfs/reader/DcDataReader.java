package com.haizhi.graph.dc.inbound.vfs.reader;

import com.haizhi.graph.dc.inbound.vfs.event.LineEvent;

import java.io.IOException;

/**
 * Created by chengangxiong on 2019/01/28
 */
public interface DcDataReader{

    LineEvent readEvent() throws IOException;
}
