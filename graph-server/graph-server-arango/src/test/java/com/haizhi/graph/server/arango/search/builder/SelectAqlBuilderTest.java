package com.haizhi.graph.server.arango.search.builder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.gdb.search.GdbQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by tanghaiyang on 2019/4/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class SelectAqlBuilderTest {

    private static final GLog LOG = LogFactory.getLogger(SelectAqlBuilderTest.class);

    private GdbQuery gdbQuery;

    @Before
    public void buildGdpQuery(){
        gdbQuery = new GdbQuery("test");
        Map<String, Set<String>> schemas = new HashMap<>();
        Set<String> listAlpha = new HashSet<>();
        schemas.put("Company",listAlpha);
        listAlpha.add("344ae9c5f3862cf40bfd6121112a9c14");
        listAlpha.add("81dcf84eea186e9c04c18b35bcf13bf6");
        listAlpha.add("f4dc00871d6c31c5ca85fd0cb4cd08ef");

        Set<String> listBeta = new HashSet<>();
        schemas.put("te_guarantee",listBeta);
        listBeta.add("JFK3L54944ADEE991715A1D521C68311");
        listBeta.add("JFK3L54944ADEE991715A1D521C68310");
        listBeta.add("PO3FL54944ADEE991715A1D521C68341");
        gdbQuery.setSchemas(schemas);
    }

    @Test
    public void buildAql(){
        Map<String, String> aqlList = IdsBuilder.get(gdbQuery);
        LOG.info("aqlList: {0}", JSON.toJSONString(aqlList, SerializerFeature.PrettyFormat));
    }

}
