package com.haizhi.graph.server.arango.search;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.server.api.gdb.search.GdbQuery;
import com.haizhi.graph.server.api.gdb.search.GdbQueryResult;
import com.haizhi.graph.server.api.gdb.search.GdbSearchDao;
import com.haizhi.graph.server.api.gdb.search.query.GraphQBuilder;
import com.haizhi.graph.server.api.gdb.search.query.QBuilders;
import com.haizhi.graph.server.arango.Database;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * Created by chengmo on 2018/1/24.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class GdbSearchDAOTest {

    @Autowired
    GdbSearchDao gdbSearchDao;

    @Test
    public void searchByGSQL(){
        GdbQuery query = new GdbQuery(Database.SEARCH);
        query.setGraphSQL("FOR u IN User SORT u.name LIMIT 2 RETURN u");
        gdbSearchDao.searchByGSQL(query);
    }

    @Test
    public void case1(){
        GdbQuery gdbQuery = new GdbQuery("crm_dev");

        /* graph builder */
        GraphQBuilder graphBuilder = QBuilders.graphQBuilder(
                Arrays.asList("Company", "Person"),
                Arrays.asList("tradable_share",
                        "officer",
                        "invest",
                        "guarantee",
                        "transfer"));
        graphBuilder.startVertices(Arrays.asList("Company/ff0b22671d213a82a255a352fced9064"));
        graphBuilder.endVertices(Arrays.asList("Company/c44cecab9ffc71d600370b276dc04f66"));
        gdbQuery.setQuery(graphBuilder);

        GdbQueryResult gdbResult = gdbSearchDao.traverse(gdbQuery);
        System.out.println(JSON.toJSONString(gdbResult, true));
    }

    @Test
    public void case2(){
        GdbQuery gdbQuery = new GdbQuery("crm_dev2");

        /* graph builder */
        GraphQBuilder graphBuilder = QBuilders.graphQBuilder(
                Arrays.asList("Company","Person"),
                Arrays.asList("te_invest"));
        graphBuilder.startVertices(Arrays.asList("Company/7908891a00d02b29354c4dd5147de439"));
        //graphBuilder.addSortOrder("invest.shareholding_ratio", Sort.Direction.DESC);
        graphBuilder.maxDepth(2);
        gdbQuery.setQuery(graphBuilder);
        System.out.println(gdbQuery.toString());

        GdbQueryResult gdbResult = gdbSearchDao.traverse(gdbQuery);
        System.out.println(JSON.toJSONString(gdbResult, true));
    }
}
