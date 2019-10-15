package com.haizhi.graph.server.tiger.search;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.server.api.gdb.search.GQuery;
import com.haizhi.graph.server.api.gdb.search.GQueryResult;
import com.haizhi.graph.server.api.gdb.search.GdbSearchBase;
import com.haizhi.graph.server.tiger.util.TigerWrapperTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by tanghaiyang on 2019/3/15.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
@EnableJpaRepositories({"com.haizhi.graph"})
@EntityScan({"com.haizhi.graph"})
@ComponentScan({"com.haizhi.graph"})
public class TigerSearchDaoImplTest {

    private static final GLog LOG = LogFactory.getLogger(TigerSearchDaoImplTest.class);

    @Autowired
    private GdbSearchBase gdbSearchBase;

    @Test
    public void search() {
        GQuery gQuery = TigerWrapperTest.buildGQuery();
        GQueryResult gQueryResult = gdbSearchBase.search(gQuery);
        LOG.info("gQueryResult:\n{0}", gQueryResult);
    }

    @Test
    public void search2() {
        String graph = "work_graph";
        String graphSql = "ls";
        GQueryResult gQueryResult = gdbSearchBase.search(graph, graphSql);
        LOG.info("gQueryResult:\n{0}", gQueryResult);
    }

    @Test
    public void graphKExpandTest(){
        String filter = FileUtils.readTxtFile("filter/graphKExpand.json");
        LOG.info(filter);
        GQuery gQuery = JSONObject.parseObject(filter, GQuery.class);
        LOG.info("gQuery: \n{0}", JSON.toJSONString(gQuery));
        GQueryResult gQueryResult = gdbSearchBase.search(gQuery);
        LOG.info("gQueryResult:\n{0}", gQueryResult);
    }

    @Test
    public void graphShortestPathTest(){


    }


    @Test
    public void graphFullPathTest(){

    }

}
