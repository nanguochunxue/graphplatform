package com.haizhi.graph.search.restapi.manager;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.rest.RestService;
import com.haizhi.graph.search.restapi.Application;
import com.haizhi.graph.sys.core.config.service.SysConfigService;
import com.haizhi.graph.sys.core.constant.UrlKeys;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by tanghaiyang on 2019/4/25.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles(value = "")
public class BaseManagerTest {

    private static final GLog LOG = LogFactory.getLogger(BaseManagerTest.class);

    @Autowired
    private SysConfigService sysUrlService;
    @Autowired
    protected RestService restService;

    @Test
    public void getUrl(){
        String url = sysUrlService.getUrl(UrlKeys.GRAPH_SEARCH_ARANGO);
        LOG.info("url:\n{0}", url);
    }
}
