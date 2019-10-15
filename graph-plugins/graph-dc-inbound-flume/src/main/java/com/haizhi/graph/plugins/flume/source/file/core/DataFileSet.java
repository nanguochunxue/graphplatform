package com.haizhi.graph.plugins.flume.source.file.core;

import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengmo on 2018/8/28.
 */
@Data
public class DataFileSet {
    // tableName
    private String schema;

    // file path list
    private List<File> files = new ArrayList<>();

    public DataFileSet(String schema){
        this.schema = schema;
    }

    public void addFile(File file){
        this.files.add(file);
    }
}
