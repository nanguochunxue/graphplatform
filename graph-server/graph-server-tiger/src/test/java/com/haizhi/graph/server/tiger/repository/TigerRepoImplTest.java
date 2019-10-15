package com.haizhi.graph.server.tiger.repository;

import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.gdb.admin.model.GdbSuo;
import com.haizhi.graph.server.api.gdb.search.GQuery;
import com.haizhi.graph.server.tiger.admin.GdbAdminDaoTest;
import com.haizhi.graph.server.tiger.util.TigerWrapper;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tanghaiyang on 2019/3/15.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
@EnableJpaRepositories({"com.haizhi.graph"})
@EntityScan({"com.haizhi.graph"})
@ComponentScan({"com.haizhi.graph"})
public class TigerRepoImplTest {

    private static final GLog LOG = LogFactory.getLogger(TigerRepoImplTest.class);

    @Autowired
    private TigerRepo tigerRepo;

    private static final String graphUrl = "http://192.168.1.101:9000/graph";
    private static final String graphSqlUrl = "http://192.168.1.101:14240/gsqlserver/gsql/file#tigergraph:tigergraph";

    /**
     * check graph exist or not
     {
     "version": {
     "api": "v2",
     "schema": 0
     },
     "error": true,
     "message": "The graph name 'work_graph1' parsed from the url = '/graph/work_graph1' is not found, please provide a valid graph name.",
     "code": "REST-1004"
     }
     *
     * */
    @Test
    public void execute(){
        String graphUrl = "http://192.168.1.101:9000/graph/work_graph1";
        JSONObject response = tigerRepo.execute(graphUrl);
        LOG.info("response: \n{0}", response);
    }

    @Test
    public void execute2(){
        String graphSql = "ls";
        JSONObject response = tigerRepo.execute(graphSqlUrl, graphSql);
        LOG.info("response: \n{0}", response);
    }

    /**
     * http://server_ip:9000/graph/{graph_name}/vertices/{vertex_type}[/{vertex_id}]?Timeout=30
    */
    @Test
    public void executeDelete(){
        String deleteUrl = "http://192.168.1.101:9000/graph/work_graph/vertices/company[/c1?Timeout=30";
        JSONObject response = tigerRepo.executeDelete(deleteUrl);
        LOG.info("response: \n{0}", response);
    }

    @Test
    public void executeUpsert(){
        GdbSuo suoVertices = GdbAdminDaoTest.buildVerticesTest();
        LOG.info("suoVertices:\n{0}",suoVertices);
        JSONObject tigerData = TigerWrapper.wrapBulk(suoVertices);
        JSONObject response = tigerRepo.executeUpsert(graphUrl, tigerData);
        LOG.info("response: \n{0}", response);
    }

    @Test
    public void executeQuery(){
        String database = "work_graph";
        String queryName = "expand_filter_pro3";
        String queryUrl = graphUrl + "/" + database + "/" + queryName;
        GQuery gQuery = TigerWrapperTest.buildGQuery();
        String gdbQuery = TigerWrapper.wrapGQuery(gQuery);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("gdbQuery", gdbQuery);
        JSONObject response = tigerRepo.executeQuery(queryUrl, parameters);
        LOG.info("response: \n{0}", response);
    }

}
