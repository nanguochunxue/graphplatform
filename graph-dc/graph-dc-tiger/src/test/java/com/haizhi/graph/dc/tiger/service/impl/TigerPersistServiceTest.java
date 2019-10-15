package com.haizhi.graph.dc.tiger.service.impl;

import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.tiger.service.TigerPersistService;
import com.haizhi.graph.server.api.gdb.admin.model.GdbSuo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghaiyang on 2019/3/8.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
@EnableJpaRepositories({"com.haizhi.graph"})
@EntityScan({"com.haizhi.graph"})
@ComponentScan({"com.haizhi.graph"})
public class TigerPersistServiceTest {

    private static final GLog LOG = LogFactory.getLogger(TigerPersistServiceTest.class);
    @Autowired
    private TigerPersistService tigerPersistService;

    @Test
    public void bulkPersist(){
//        GdbSuo suo = buildVerticesTest();
        DcInboundDataSuo dcSuo = new DcInboundDataSuo();
        CudResponse response = tigerPersistService.bulkPersist(dcSuo);
        LOG.info("response: {}", response);
    }

    /**
     * do not support DcInboundDataSuo, beacuse it have no SchemaType
     */
    @Test
    public void bulkPersistDc(){
    }

    public static GdbSuo buildVerticesTest(){
        GdbSuo suo = new GdbSuo();
        suo.setGraph("work_graph");
        suo.setSchema("persons");
        suo.setType(SchemaType.VERTEX);
        suo.setOperation(GOperation.UPDATE);

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put(Keys.OBJECT_KEY, "p100");
        map1.put("city", "dongguan");
        map1.put("quadrant", 2);
        map1.put("company_name", "com2");
        map1.put("sex", "male");
        map1.put("id", "c2");
        map1.put("age", 2);
        rows.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put(Keys.OBJECT_KEY, "p101");
        map2.put("city", "sanya");
        map2.put("quadrant", 1);
        map2.put("company_name", "com1");
        map2.put("sex", "female");
        map2.put("id", "c1");
        map2.put("age", 1);
        rows.add(map2);
        suo.setRows(rows);

        return suo;
    }

    public GdbSuo buildGdbData(DcInboundDataSuo cuo) {


        return null;
    }
}
