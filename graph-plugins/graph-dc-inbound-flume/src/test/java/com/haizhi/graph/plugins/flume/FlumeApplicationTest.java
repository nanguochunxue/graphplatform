package com.haizhi.graph.plugins.flume;

import com.haizhi.graph.plugins.flume.source.taildir.ApplicationTest;
import org.junit.Test;

import java.net.URL;

/**
 * Created by chengangxiong on 2018/12/18
 */
public class FlumeApplicationTest {

    @Test
    public void test() throws InterruptedException {

        URL testConfig = ApplicationTest.class.getClassLoader().getResource("inbound-flume.properties");

        String agentName = "agent";

        FlumeApplication.main(new String[]{"-n" + agentName,"-f" + "/Users/haizhi/IdeaProjects/graph/graph-plugins/graph-dc-inbound-flume/src/main/resources/flume-config.properties"});

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Test
    public void testTaildir() throws InterruptedException {

        String agentName = "agent";

        String configFile = "/Users/haizhi/IdeaProjects/graph/graph-plugins/graph-dc-inbound-flume/src/main/resources/flume-config.properties";

        FlumeApplication.main(new String[]{"-n" + agentName,"-f" + configFile});

        Thread.sleep(Integer.MAX_VALUE);
    }
}