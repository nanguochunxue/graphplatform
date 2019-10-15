package com.haizhi.graph.server.hdfs;

import com.google.common.base.Preconditions;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.bean.StoreURL;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by chengmo on 2018/3/14.
 */
public class HDFSHelper implements Closeable{

    private static final GLog LOG = LogFactory.getLogger(HDFSHelper.class);
    private static final String FS_DEFAULT = "fs.defaultFS";
    private Configuration conf;
    private HDFSConfig config;

    private FileSystem fs;

    public HDFSHelper(String locationConfig) {
        this.config = new HDFSConfig(locationConfig);
        conf = config.getConf();
        try {
            fs = FileSystem.get(conf);
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    public HDFSHelper(StoreURL storeURL) {
        if (storeURL.getFilePath() == null){
            conf.addResource("core-site.xml");
            conf.addResource("hdfs-site.xml");
            try {
                fs = FileSystem.get(conf);
            } catch (IOException e) {
                LOG.error(e);
            }
        }else {
            Map<String, String> fileConfig = storeURL.getFilePath();
            conf = new Configuration();
            conf.addResource(new Path(fileConfig.get("core-site.xml")));
            conf.addResource(new Path(fileConfig.get("hdfs-site.xml")));
            try {
                if (storeURL.isSecurityEnabled()){
                    if ("kerberos".equalsIgnoreCase(conf.get("hadoop.security.authentication"))) {
                        LoginUtil.login(storeURL.getUserPrincipal(), fileConfig.get("user.keytab"), fileConfig.get("krb5.conf"), conf);
                    }
                }
                fs = FileSystem.get(conf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public HDFSHelper(String url, Map<String, String> fileConfig) {
        conf = new Configuration();
        conf.addResource(new Path(fileConfig.get("core-site.xml")));
        conf.addResource(new Path(fileConfig.get("hdfs-site.xml")));
        if (StringUtils.isNotEmpty(url)){
            conf.set(FS_DEFAULT, url);
        }
        try {
            fs = FileSystem.get(conf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // only for auto gen url
    public static final String autoGenURL(Map<String, String> fileConfig) {
        Configuration tmpConf = new Configuration();
        tmpConf.addResource(fileConfig.get("core-site.xml"));
        tmpConf.addResource(fileConfig.get("hdfs-site.xml"));
        return tmpConf.get(FS_DEFAULT, "");
    }

    public String getFsURI() {
        return conf.get(FS_DEFAULT, "");
    }

    public void readLine(String path, Consumer<String> lineConsumer) {
        Path p = new Path(path);
        try {
            if (!fs.exists(p) || fs.getFileStatus(p).isDirectory()) {
                return;
            }
            FSDataInputStream input = fs.open(p);
            InputStreamReader inr = new InputStreamReader(input);
            BufferedReader read = new BufferedReader(inr);
            String line;
            while ((line = read.readLine()) != null) {
                lineConsumer.accept(line);
            }
        } catch (IOException e) {
            LOG.error(e);
        }
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

    public long upload(InputStream inputStream, Path dst) {
        long uploadDataLength = -1L;
        try {
            if (!fs.exists(dst.getParent())) {
                fs.mkdirs(dst);
            }
            try (OutputStream outputStream = fs.create(dst).getWrappedStream()) {
                uploadDataLength = IOUtils.copy(inputStream, outputStream);
                LOG.info("Success to upload stream - dstPath[{0}],length[{1}]", dst.toString(), uploadDataLength);
            }
        } catch (IOException e) {
            LOG.error(e);
        }
        return uploadDataLength;
    }

    public void download(String filePath, OutputStream outputStream) {
        try {
            Path path = new Path(filePath);
            if (!fs.exists(path)){
                throw new RuntimeException("file not exist");
            }
            try (InputStream inputStream = fs.open(path).getWrappedStream()){
                IOUtils.copy(inputStream, outputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public void listAndPrint(String path) {
        if (!exsits(path)){
            LOG.info("path not exits");
        }
        try {
            FileStatus[] status = fs.listStatus(new Path(path));
            for (int i = 0; i < status.length; ++i){
                System.out.println(status[i].getPath().toString());
            }
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    public void testConnect() {
        Path path = new Path("/graph");
        try {
            fs.exists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                fs.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public List<String> readAllLine(String path) throws IOException {
        LinkedList<String> lines = new LinkedList<>();
        Path p = new Path(path);
        Preconditions.checkArgument(fs.exists(p), "file not exists");
        try (FSDataInputStream input = fs.open(p);
             InputStream wrappedStream = input.getWrappedStream();
             InputStreamReader inputStreamReader = new InputStreamReader(wrappedStream);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
}
