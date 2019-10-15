package com.haizhi.graph.plugins.flume;

import com.haizhi.graph.plugins.flume.sink.SimpleGapSink;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chengangxiong on 2018/12/27
 */
public class BatchApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(SimpleGapSink.class);

    @Test
    public void test(){
        String graph = "graph4";
        String filePath = "/Users/haizhi/IdeaProjects/graph/graph-plugins/graph-dc-inbound-flume/src/main/resources/batch-config.properties";
        BatchApplication application = new BatchApplication();
        application.configAndRun(graph, filePath);
    }
}