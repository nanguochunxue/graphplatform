package com.haizhi.graph.dc.core.service;

import com.haizhi.graph.dc.core.bean.Domain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengangxiong on 2019/01/07
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi-fi")
public class DcMetadataCacheTest {

    @Autowired
    private DcMetadataCache dcMetadataCache;

    @Test
    public void getDomain(){
        Domain domain = dcMetadataCache.getDomain("demo_graph");
        assert domain.getGraphName() != null;
    }

}