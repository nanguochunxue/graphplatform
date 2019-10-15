package com.haizhi.graph.dc.es.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * Created by chengangxiong on 2019/01/03
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class EsPersistServiceTest {

    @Autowired
    private EsPersistService esPersistService;

    @Test
    public void bulkPersist() {

        DcInboundDataSuo inboundDataCuo = new DcInboundDataSuo();

        inboundDataCuo.setOperation(GOperation.CREATE_OR_UPDATE);
        inboundDataCuo.setGraph("hello_fi");
        inboundDataCuo.setSchema("world_fi");
        List<Map<String, Object>> data = Lists.newArrayListWithCapacity(5);
        Map<String, Object> map1 = Maps.newHashMap();
        map1.put("object_key", "331");

        data.add(map1);
        inboundDataCuo.setRows(data);

        CudResponse response = esPersistService.bulkPersist(inboundDataCuo);
        assert response.isSuccess();
    }
}