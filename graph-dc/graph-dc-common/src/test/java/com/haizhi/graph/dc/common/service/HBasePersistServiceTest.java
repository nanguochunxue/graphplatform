package com.haizhi.graph.dc.common.service;

import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.admin.HBaseAdminDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * Created by chengmo on 2018/5/14.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class HBasePersistServiceTest {

    private static final String GRAPH = "graph_one";
    private static final String SCHEMA = "schema_from";

    @Autowired
    private HBasePersistService hBasePersistService;

    @Autowired
    private HBaseAdminDao hBaseAdminDao;

    private StoreURL storeURL;

    @Test
    public void saveDataForEdgeMerge() {
        DcInboundDataSuo dto = this.getDcInboundDataCuo();
        dto.setOperation(GOperation.CREATE_OR_UPDATE);
        CudResponse response = hBasePersistService.bulkPersist(dto);
        assert response.isSuccess();
    }

//    @Before
    public void dataInitialize() {
        if (!hBaseAdminDao.existsDatabase(storeURL, GRAPH)) {
            hBaseAdminDao.createDatabase(storeURL, GRAPH);
        }
        if (!hBaseAdminDao.existsTable(storeURL, GRAPH, SCHEMA)) {
            hBaseAdminDao.createTable(storeURL, GRAPH, SCHEMA, true);
        }
    }

//    @After
    public void cleanup() {
        if (hBaseAdminDao.existsTable(storeURL, GRAPH, SCHEMA)) {
            //hBaseAdminDao.deleteTable(GRAPH, SCHEMA);
        }
    }

    private DcInboundDataSuo getDcInboundDataCuo() {
        DcInboundDataSuo dto = new DcInboundDataSuo();
        dto.setGraph(GRAPH);
        dto.setSchema(SCHEMA);

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put(Keys._ROW_KEY, "000#1");
        row.put("name", "houyi");
        row.put("age", 300);
        rows.add(row);

        row = new HashMap<>();
        row.put(Keys._ROW_KEY, "000#2");
        row.put("name", "luban");
        row.put("age", 301);
        rows.add(row);
        dto.setRows(rows);
        return dto;
    }
}
