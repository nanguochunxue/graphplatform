package com.haizhi.graph.server.arango.search;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Sets;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.gdb.search.GdbQuery;
import com.haizhi.graph.server.api.gdb.search.GdbQueryResult;
import com.haizhi.graph.server.api.gdb.search.GdbSearchDao;
import com.haizhi.graph.server.api.gdb.search.query.GraphQBuilder;
import com.haizhi.graph.server.api.gdb.search.query.QBuilders;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * Created by tanghaiyang on 2019/4/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class ArangoSearchDaoTest {

    private static final GLog LOG = LogFactory.getLogger(ArangoSearchDaoImpl.class);

    @Autowired
    private GdbSearchDao gdbSearchDao;

    private StoreURL storeURL;
    private GdbQuery gdbQuery;
    private GdbQuery gdbQueryExpand;
    private GdbQuery gdbQueryFindByIds;

    @Before
    public void buildStoreURL(){
        storeURL = new StoreURL();
        storeURL.setUrl("192.168.1.176:8529");
        storeURL.setUser("root");
        storeURL.setPassword("123456");
    }

    @Before
    public void buildGdbQuery(){
        gdbQuery = new GdbQuery("test");
        Set<String> vertexTables = Sets.newHashSet("Company","Person");
        Set<String> edgeTables = Sets.newHashSet("te_guarantee", "te_invest", "te_officer", "te_transfer");
        Set<String> startVertices = Sets.newHashSet("Company/7908891a00d02b29354c4dd5147de439");
        Set<String> endVertices = Sets.newHashSet("Company/36d37c063ee31a5aebcc3667af028715");
        GraphQBuilder builder = QBuilders.graphQBuilder(vertexTables, edgeTables);
        builder.startVertices(startVertices);
        builder.endVertices(endVertices);
        builder.direction("any");
        builder.maxDepth(1);
        builder.offset(0);
        builder.size(10);
        gdbQuery.setQueryBuilder(builder);
    }

    @Before
    public void buildGdbQueryExpand(){
        gdbQueryExpand = new GdbQuery("test");
        Set<String> vertexTables = Sets.newHashSet("Company","Person");
        Set<String> edgeTables = Sets.newHashSet("te_guarantee", "te_invest", "te_officer", "te_transfer");
        Set<String> startVertices = Sets.newHashSet("Company/7908891a00d02b29354c4dd5147de439");
        GraphQBuilder builder = QBuilders.graphQBuilder(vertexTables, edgeTables);
        builder.startVertices(startVertices);
        builder.direction("any");
        builder.maxDepth(1);
        builder.offset(0);
        builder.size(10);
        gdbQueryExpand.setQueryBuilder(builder);
    }

    @Before
    public void buildGdbQueryFindByIds(){
        gdbQueryFindByIds = new GdbQuery("test");
        Map<String, Set<String>> schemas = new HashMap<>();
        Set<String> listAlpha = new HashSet<>();
        schemas.put("Company", listAlpha);
        listAlpha.add("344ae9c5f3862cf40bfd6121112a9c14");
        listAlpha.add("81dcf84eea186e9c04c18b35bcf13bf6");
        listAlpha.add("f4dc00871d6c31c5ca85fd0cb4cd08ef");

        Set<String> listBeta = new HashSet<>();
        schemas.put("te_guarantee", listBeta);
        listBeta.add("JFK3L54944ADEE991715A1D521C68311");
        listBeta.add("JFK3L54944ADEE991715A1D521C68310");
        listBeta.add("PO3FL54944ADEE991715A1D521C68341");
        gdbQueryFindByIds.setSchemas(schemas);
        gdbQueryFindByIds.setSchemas(schemas);
    }

    @Test
    public void traverse(){
        GdbQueryResult gdbResult = gdbSearchDao.traverse(storeURL, gdbQueryExpand);
        LOG.info("traverse gdbResult: {0}", JSON.toJSONString(gdbResult,SerializerFeature.PrettyFormat));
    }

    @Test
    public void shortestPath(){
        GdbQueryResult gdbResult = gdbSearchDao.shortestPath(storeURL, gdbQuery);
        LOG.info("shortestPath gdbResult: {0}", JSON.toJSONString(gdbResult,SerializerFeature.PrettyFormat));
    }

    @Test
    public void fullPath(){
        GdbQueryResult gdbResult = gdbSearchDao.traverse(storeURL, gdbQuery);
        LOG.info("fullPath gdbResult: {0}", JSON.toJSONString(gdbResult,SerializerFeature.PrettyFormat));
    }

    @Test
    public void findByIds(){
        GdbQueryResult queryResult = gdbSearchDao.findByIds(storeURL, gdbQueryFindByIds);
        LOG.info("findByIds queryResult:{0}", JSON.toJSONString(queryResult, SerializerFeature.PrettyFormat));
    }

}
