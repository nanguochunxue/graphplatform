package com.haizhi.graph.plugins.flume.sink;


import org.apache.flume.Context;
import org.apache.flume.FlumeException;
import org.apache.flume.formatter.output.DefaultPathManager;
import org.apache.flume.formatter.output.PathManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by chengangxiong on 2018/12/18
 * <p>
 * deal with the DefaultPathManager while the current file not exists issue
 */
public class CustomPathManager extends DefaultPathManager {
    public CustomPathManager(Context context) {
        super(context);
    }

    @Override
    public File getCurrentFile() {
        File file = super.getCurrentFile();
        if (!file.exists()) {
            try {
                Files.createDirectories(Paths.get(file.getPath()).getParent());
                file.createNewFile();
            } catch (IOException e) {
                throw new FlumeException("cannot create file : " + file.getPath());
            }
        }
        return file;
    }

    public static class Builder implements PathManager.Builder {
        @Override
        public PathManager build(Context context) {
            return new CustomPathManager(context);
        }
    }
}
