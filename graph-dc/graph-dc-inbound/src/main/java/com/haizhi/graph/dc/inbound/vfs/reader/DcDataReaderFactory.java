package com.haizhi.graph.dc.inbound.vfs.reader;


import com.haizhi.graph.dc.inbound.vfs.DcFileObject;
import com.haizhi.graph.dc.inbound.vfs.exception.DcFileException;

/**
 * Created by chengangxiong on 2019/01/28
 */
public class DcDataReaderFactory {
    public static DcDataReader getReader(DcFileObject dcFileObject) {
        String fileFormat = dcFileObject.getFileFormat();
        switch (fileFormat) {
            case "csv":
                return new CsvReader(dcFileObject);
            case "json":
            case "txt":
                return new JsonReader(dcFileObject);
            case "line":
                return new LineReader(dcFileObject);
            default:
                throw new DcFileException("not supported file format[" + fileFormat + "] that specified by last .(dot) ");
        }
    }
}
