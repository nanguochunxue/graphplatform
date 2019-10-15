package com.haizhi.graph.dc.inbound.vfs.reader;

import com.haizhi.graph.dc.inbound.vfs.DcFileObject;
import com.haizhi.graph.dc.inbound.vfs.event.LineEvent;
import com.haizhi.graph.dc.inbound.vfs.exception.DcFileException;

import java.io.IOException;

/**
 * Created by chengangxiong on 2019/01/28
 */
public class CsvReader extends AbstractDataReader {

    public CsvReader(DcFileObject dcFileObject) {
        super(dcFileObject);
    }

    @Override
    protected void init(){
        super.init();
        try {
            String firstLine = dcFileObject.readFirstLine();
            header.put(LineEvent.CSV_HEADER, firstLine);
        } catch (IOException e) {
            throw new DcFileException("init csv file error", e);
        }
    }

    @Override
    public LineEvent readEvent() throws IOException {
        String line = dcFileObject.readLine();
        if (line == null){
            return null;
        }
        return LineEvent.Builder.with(line,  getHeaders());
    }
}
