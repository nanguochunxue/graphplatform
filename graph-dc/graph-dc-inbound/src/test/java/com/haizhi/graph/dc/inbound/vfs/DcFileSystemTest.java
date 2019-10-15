package com.haizhi.graph.dc.inbound.vfs;

import com.haizhi.graph.dc.inbound.vfs.event.LineEvent;
import org.apache.commons.vfs2.FileSystemException;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by chengangxiong on 2019/02/19
 */
public class DcFileSystemTest {

    @Test
    public void test(){
        String originalFileName = "/tmp/vfs_file/a.zip";
        int lastDotIndex = originalFileName.lastIndexOf(".");
        String fileType = originalFileName.substring(lastDotIndex);

        System.out.println(fileType); // .zip
    }

    @Test
    public void testLocal() throws IOException {
        String source1 = "zip:file://Users/haizhi/tmp/vfs_file/b.zip!";
        String source2 = "zip:file://Users/haizhi/tmp/vfs_file/a.zip!";
        DcFileSystem fileSystem = new DcFileSystem();
        fileSystem.addSource(source1);
        fileSystem.addSource(source2);
        LineEvent lineEvent= null;

        while ((lineEvent = fileSystem.readEvent()) != null){
            System.out.println(lineEvent);
        }
    }

    @Test
    public void test2() throws IOException {

        DcFileSystem fileSystem = new DcFileSystem();

        String s1 = "zip:hdfs://hadoop01.sz.haizhi.com:8020/user/graph/task_115c254d-57e3-4193-ba3a-7bb8190d0f4a.zip!";
//        String s2 = "hdfs://hadoop01.sz.haizhi.com:8020/user/graph/task_968f3cb6-5424-431c-bb47-67cde7f88043.zip";
        fileSystem.addSource(s1);
//        fileSystem.addSource("zip:" + s2 + "!");
        doRead(fileSystem);

    }

    private void doRead(DcFileSystem fileSystem) {
        try {
            int total = 0;
            LineEvent lineEvent= null;

            while ((lineEvent = fileSystem.readEvent()) != null){
                System.out.println(lineEvent);
                total ++;
            }
            System.out.println("total : " + total);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test3() throws FileSystemException {
        String s1 = "hdfs://hadoop01.sz.haizhi.com:8020/user/graph/task_99402211-4e78-41f2-9d94-76f2666c2f34.txt";
        String s2 = "hdfs://hadoop01.sz.haizhi.com:8020/user/graph/task_9647249d-0aae-419b-b117-e93f10253160.json";
        String s3 = "hdfs://hadoop01.sz.haizhi.com:8020/user/graph/task_3d9c06fc-3b10-41d2-876a-bb22cd9e136b.csv";
        DcFileSystem fileSystem = new DcFileSystem();
        fileSystem.addSource(s1);
        fileSystem.addSource(s2);
//        fileSystem.addSource(s3);
        doRead(fileSystem);
    }
}