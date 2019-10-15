package com.haizhi.graph.tag.analytics.engine.driver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengmo on 2018/7/28.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi-fic80")
public class SimpleDriverTest {

    @Test
    public void run() throws Exception {
        SimpleDriver.main(new String[]{"debug"});
    }
}
