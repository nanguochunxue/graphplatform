package com.haizhi.graph.dc.inbound.vfs;

import com.haizhi.graph.dc.inbound.vfs.event.LineEvent;
import com.haizhi.graph.dc.inbound.vfs.exception.DcFileException;
import com.haizhi.graph.dc.inbound.vfs.reader.DcDataReader;
import com.haizhi.graph.dc.inbound.vfs.reader.DcDataReaderFactory;
import lombok.NoArgsConstructor;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by chengangxiong on 2019/01/28
 */
@NoArgsConstructor
public class DcFileSystem {

    private final LinkedList<DcDataReader> readerLinkedList = new LinkedList<>();

    private DcDataReader currReader;

    public DcFileSystem(String uri){
        addSource(uri);
    }

    public void addSource(String source){
        try {
            FileSystemManager fileSystemManager = VFS.getManager();
            FileObject fileObject = fileSystemManager.resolveFile(source);
            init(fileObject);
        } catch (FileSystemException e) {
            throw new DcFileException(e);
        }
    }

    private void init(FileObject fileObject) throws FileSystemException {
        if(fileObject.isFile()){
            if (fileObject.isReadable()){
                readerLinkedList.add(DcDataReaderFactory.getReader(new DcFileObject(fileObject)));
            } else {
                throw new DcFileException("file[" + fileObject.getName().getPath() + "] is unReadable");
            }
        }else{
            for (FileObject fo : fileObject.getChildren()){
                init(fo);
            }
        }
    }

    public LineEvent readEvent() throws IOException {
        DcDataReader dcFileObject = currReader;
        if (dcFileObject == null){
            if (readerLinkedList.isEmpty()){
                return null;
            }else {
                currReader = readerLinkedList.pollFirst();
                return readEvent();
            }
        }
        LineEvent line;
        if ((line = dcFileObject.readEvent()) != null){
            return line;
        }
        currReader = readerLinkedList.pollFirst();
        return readEvent();
    }
}
