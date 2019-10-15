package com.haizhi.graph.dc.inbound.vfs;

import com.haizhi.graph.dc.inbound.vfs.access.ReadLine;
import com.haizhi.graph.dc.inbound.vfs.access.random.RandomAccessReadLine;
import com.haizhi.graph.dc.inbound.vfs.access.stream.StreamReadLine;
import com.haizhi.graph.dc.inbound.vfs.exception.DcFileException;
import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.util.RandomAccessMode;

import java.io.IOException;

/**
 * Created by chengangxiong on 2019/01/28
 */
public class DcFileObject {

    private FileObject fileObject;

    private ReadLine readLine;

    private String fileFormat;

    public DcFileObject(FileObject fileObject) {
        try {
            this.fileObject = fileObject;
            if (fileObject.getFileSystem().hasCapability(Capability.RANDOM_ACCESS_READ)) {
                readLine = new RandomAccessReadLine(fileObject.getContent().getRandomAccessContent(RandomAccessMode.READ));
            }else {
                readLine = new StreamReadLine(fileObject.getContent().getInputStream());
            }
            String path = fileObject.getName().getPath();
            fileFormat = path.substring(path.lastIndexOf(".") + 1);
        } catch (FileSystemException e) {
            throw new DcFileException(e);
        }
    }

    public String readLine() throws IOException {
        return readLine.readLine();
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public String readFirstLine() throws IOException {

        return readLine.firstLine();
    }

    public String getPath() {
        return fileObject.getName().toString();
    }
}
