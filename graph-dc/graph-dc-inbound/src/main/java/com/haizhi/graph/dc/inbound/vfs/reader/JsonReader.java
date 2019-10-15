package com.haizhi.graph.dc.inbound.vfs.reader;

import com.haizhi.graph.dc.inbound.vfs.DcFileObject;
import com.haizhi.graph.dc.inbound.vfs.event.LineEvent;

import java.io.IOException;

/**
 * Created by chengangxiong on 2019/01/28
 */
public class JsonReader extends AbstractDataReader {

    public JsonReader(DcFileObject dcFileObject) {
        super(dcFileObject);
    }

    @Override
    public LineEvent readEvent() throws IOException {
        return LineEvent.Builder.with(dcFileObject.readLine(), getHeaders());
    }
}
