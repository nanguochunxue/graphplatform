package com.haizhi.graph.plugins.flume.source.taildir;

/**
 * Created by chengangxiong on 2019/01/15
 */
public class FileFinder {

    private String scanDir;

    private String filePattern;

    public FileFinder(String scanDir, String filePattern){
        this.scanDir = scanDir;
        this.filePattern = filePattern;
    }

    public String getScanDir() {
        return scanDir;
    }

    public String getFilePattern() {
        return filePattern;
    }

    @Override
    public String toString() {
        return "scanDir = " + scanDir + ", filePattern = " + filePattern;
    }
}
