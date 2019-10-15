package com.haizhi.graph.tag.analytics.task.context;

import com.haizhi.graph.common.context.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengmo on 2018/4/20.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class TagHadoopContextTest {

    @Test
    public void createHiveTables() {
        String graph = "crm_dev";
        TagHadoopContext.createHiveTables(graph, Resource.getActiveProfile());
    }

    @Test
    public void createEsIndex() {
        String graph = "crm_dev";
        TagHadoopContext.createEsIndex(graph);
    }
}
