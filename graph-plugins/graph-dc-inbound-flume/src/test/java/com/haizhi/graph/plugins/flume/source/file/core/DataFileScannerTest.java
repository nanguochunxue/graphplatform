package com.haizhi.graph.plugins.flume.source.file.core;

import com.haizhi.graph.common.context.Resource;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.plugins.flume.source.file.core.DataFileScanner;
import com.haizhi.graph.plugins.flume.source.file.core.DataFileSet;
import com.haizhi.graph.plugins.flume.source.file.core.DataPartition;
import org.junit.Test;

import java.util.Map;

/**
 * Created by chengmo on 2018/11/30.
 */
public class DataFileScannerTest {

    @Test
    public void example() throws Exception {
        String rootDir = Resource.getResourcePath("/data");
        DataFileScanner fileScanner = new DataFileScanner(rootDir);
        Map<String, DataFileSet> resultMap = fileScanner.scan();
        JSONUtils.println(resultMap);
    }

    @Test
    public void exampleByPartition() throws Exception {
        String rootDir = Resource.getResourcePath("/data");
        DataFileScanner fileScanner = new DataFileScanner(rootDir, DataPartition.DAY$HOUR_BEFORE);
        System.out.println(fileScanner.getPath());
    }
}
