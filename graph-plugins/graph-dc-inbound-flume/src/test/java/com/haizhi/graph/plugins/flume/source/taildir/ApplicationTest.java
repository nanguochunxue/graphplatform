package com.haizhi.graph.plugins.flume.source.taildir;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import org.apache.flume.lifecycle.LifecycleAware;
import org.apache.flume.node.Application;
import org.apache.flume.node.PollingPropertiesFileConfigurationProvider;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Created by chengangxiong on 2018/12/17
 */
public class ApplicationTest {

    @Test
    public void test() throws InterruptedException {

        URL testConfig = ApplicationTest.class.getClassLoader().getResource("inbound-flume.properties");

        String agentName = "agent";
        File configFile = new File(testConfig.getFile());

        EventBus eventBus = new EventBus("test-event-bus");
        PollingPropertiesFileConfigurationProvider configurationProvider =
                new PollingPropertiesFileConfigurationProvider(agentName,
                        configFile, eventBus, 1);
        List<LifecycleAware> components = Lists.newArrayList();
        components.add(configurationProvider);
        Application application = new Application(components);
        eventBus.register(application);
        application.start();
//      Thread.sleep(random.nextInt(10000));
        Thread.sleep(1000L * 600);
        application.stop();

    }
}
