package com.haizhi.graph.plugins.flume.source.file.core;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by chengmo on 2018/11/30.
 */
public class DataFileScanner {
    private static final GLog LOG = LogFactory.getLogger(DataFileScanner.class);

    private String path;
    private DataPartition partitionedBy;

    public DataFileScanner(String path) {
        this.path = path;
    }

    public DataFileScanner(String path, DataPartition partitionedBy) {
        this.path = path;
        if (!path.endsWith(File.separator)) {
            this.path += File.separator;
        }
        this.partitionedBy = partitionedBy;
        if (!Objects.isNull(partitionedBy)) {
            this.path += DataPartition.getPath(partitionedBy);
        }
    }

    /**
     * Example:
     *  |- <path>
     *      |-Company           --schema name vertex
     *          |-part-0001     --schema file
     *          |-part-0002
     *      |-invest            --schema name edge
     *          |-part-0001     --schema file
     *
     * @return
     * @throws Exception
     */
    public Map<String, DataFileSet> scan() throws Exception {
        File rootDir = new File(path);
        if (!rootDir.isDirectory() || !rootDir.canRead()) {
            throw new IllegalAccessException("path does not directory or cannot read: " + path);
        }
        return doScan(rootDir);
    }

    public String getPath() {
        return this.path;
    }

    public DataPartition getPartitionedBy() {
        return this.partitionedBy;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private Map<String, DataFileSet> doScan(File rootDir) throws Exception {
        Map<String, DataFileSet> resultMap = new HashMap<>();
        LOG.info("Start scanning directory: {0}", rootDir.getPath());
        // schema list
        File[] subDirList = rootDir.listFiles();
        for (File subDir : subDirList) {
            if (!subDir.isDirectory() || !subDir.canRead()) {
                LOG.warn("oes not directory or cannot read: {0}", subDir.getPath());
                continue;
            }
            String schema = subDir.getName();
            DataFileSet fileSet = resultMap.get(schema);
            if (fileSet == null) {
                fileSet = new DataFileSet(schema);
                resultMap.put(schema, fileSet);
            }
            // schema including files
            File[] files = subDir.listFiles();
            for (File file : files) {
                fileSet.addFile(file);
            }
        }
        return resultMap;
    }
}
