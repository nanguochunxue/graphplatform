package com.haizhi.graph.engine.flow.tools.hdfs;

import com.google.common.base.Preconditions;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.engine.flow.conf.FKeys;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by chengmo on 2018/3/14.
 */
public class HDFSHelper implements Closeable {

    private static final GLog LOG = LogFactory.getLogger(HDFSHelper.class);
    private static Configuration conf = new Configuration();

    private FileSystem fs;

    public HDFSHelper() {
        conf.addResource("core-site.xml");
        conf.addResource("hdfs-site.xml");
        try {
            fs = FileSystem.get(conf);
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    public HDFSHelper(String hadoopConfDir) {
        try {
            hadoopConfDir = hadoopConfDir.endsWith(File.separator) ? hadoopConfDir :
                    hadoopConfDir + File.separator;
            conf.addResource(new Path(hadoopConfDir + "core-site.xml"));
            conf.addResource(new Path(hadoopConfDir + "hdfs-site.xml"));
            fs = FileSystem.get(conf);
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    public static String getFsURI() {
        return conf.get(FKeys.FS_DEFAULT, "");
    }

    public void readLine(String path, Consumer<String> lineConsumer) throws IOException {
        Path p = new Path(path);
        Preconditions.checkArgument(fs.exists(p), "file not exists");
        try (FSDataInputStream input = fs.open(p);
             InputStream wrappedStream = input.getWrappedStream();
             InputStreamReader inputStreamReader = new InputStreamReader(wrappedStream);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineConsumer.accept(line);
            }
        }
    }

    public String readLine(String path) {
        Path p = new Path(path);
        String result = "";
        try {
            if (!fs.exists(p) || fs.getFileStatus(p).isDirectory()) {
                return result;
            }
            FSDataInputStream input = fs.open(p);
            InputStreamReader inr = new InputStreamReader(input);
            BufferedReader read = new BufferedReader(inr);
            String line;
            while ((line = read.readLine()) != null) {
                result = line;
                break;
            }
        } catch (IOException e) {
            LOG.error(e);
        }
        return result;
    }

    public boolean upsertLine(String path, String text) {
        boolean success = true;
        try {
            FSDataOutputStream output = fs.create(new Path(path));
            output.write(text.getBytes());
            output.close();
            LOG.info("Success to upsertLine path[{0}]", path);
        } catch (IOException e) {
            success = false;
            LOG.error(e);
        }
        return success;
    }

    public boolean isEmptyDir(String path) {
        boolean flag = true;
        Path p = new Path(path);
        try {
            if (!fs.exists(p) || !fs.getFileStatus(p).isDirectory()) {
                return flag;
            }
            FileStatus[] files = fs.listStatus(p);
            flag = files.length == 0;
        } catch (IOException e) {
            LOG.error(e);
        }
        return flag;
    }

    public boolean exsits(String path) {
        boolean success = true;
        try {
            success = fs.exists(new Path(path));
        } catch (IOException e) {
            success = false;
            LOG.error(e);
        }
        return success;
    }

    public boolean delete(String path) {
        boolean success = true;
        try {
            Path p = new Path(path);
            if (!fs.exists(p)) {
                LOG.info("Path does not exist, [{0}]", path);
                return false;
            }
            fs.delete(p, true);
            LOG.info("Success to delete path[{0}]", path);
        } catch (IOException e) {
            success = false;
            LOG.error("Delete path error.", e);
        }
        return success;
    }

    public void close() {
        try {
            if (fs != null) {
                fs.close();
            }
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    public boolean upload(String localPath, String dstPath) {
        boolean flag = true;
        Path src = new Path(localPath);
        try {
            FileSystem local = FileSystem.getLocal(conf);
            if (!local.exists(src)) {
                LOG.warn("local path does not exists, {0}", localPath);
                return flag;
            }
            Path dst = new Path(dstPath);
            if (!fs.exists(dst)) {
                fs.mkdirs(dst);
            }
            if (!fs.isDirectory(dst)) {
                LOG.warn("hdfs path is not directory, {0}", dstPath);
                return flag;
            }
            if (!local.getFileStatus(src).isDirectory()) {
                fs.copyFromLocalFile(false, src, dst);
                LOG.info("Success to upload local file - {0}", src);
                return flag;
            }
            FileStatus[] files = local.listStatus(src);
            for (FileStatus file : files) {
                if (file.isDirectory()) {
                    continue;
                }
                fs.copyFromLocalFile(false, file.getPath(), dst);
                LOG.info("Success to upload local file - {0}", file.getPath());
            }
        } catch (IOException e) {
            LOG.error(e);
        }
        return flag;
    }

    public long upload(InputStream inputStream, Path dst) {
        long uploadDataLength = -1L;
        try {
            if (!fs.exists(dst.getParent())) {
                fs.mkdirs(dst);
            }
            try (OutputStream outputStream = fs.create(dst).getWrappedStream();) {
                uploadDataLength = IOUtils.copy(inputStream, outputStream);
                LOG.info("Success to upload stream - dstPath[{0}],length[{1}]", dst.toString(), uploadDataLength);
            }
        } catch (IOException e) {
            LOG.error(e);
        }
        return uploadDataLength;
    }

    public void listAndPrint(String path) {
        if (!exsits(path)) {
            LOG.info("path not exits");
        }
        try {
            FileStatus[] status = fs.listStatus(new Path(path));
            for (int i = 0; i < status.length; ++i) {
                System.out.println(status[i].getPath().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Configuration conf = new Configuration();
        System.out.println(conf.get(FKeys.FS_DEFAULT));
        HDFSHelper helper = new HDFSHelper();
        //String path = "/user/graph/tag/tag-domain.json";
        //String text = "testttffffffff";
        //helper.upsertLine("/user/graph/tag/TAG.DAG=>logic.2_21_1.tags[3]", text);
        //System.out.println(helper.readLine(path));
        //System.out.println(helper.isEmptyDir("/user/graph/lib/spark2x.jars/"));
        //System.setProperty("HADOOP_USER_NAME", "admin");
        //String localPath = "/Users/haizhi/MyData/Opensource/Hadoop/Spark/spark-2.1.0-bin-hadoop2.7/jars";
        //String dstPath = "/user/graph/lib/spark2x.jars1/";
        //helper.upload(localPath, dstPath);
        helper.close();
    }
}
