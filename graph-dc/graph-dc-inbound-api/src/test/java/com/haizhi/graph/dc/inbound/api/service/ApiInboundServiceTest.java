package com.haizhi.graph.dc.inbound.api.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.model.v1.Response;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.service.DcMetadataCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by chengangxiong on 2019/01/07
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi-fi")
public class ApiInboundServiceTest {

    @Autowired
    private ApiInboundService apiInboundService;

    @Autowired
    private DcMetadataCache dcMetadataCache;

    @Test
    public void graph(){
        Domain domain = dcMetadataCache.getDomain("fi_graph");
        System.out.println("print schema data -----");
        System.out.println(domain.getSchemaMap());

        System.out.println("print store data -----");
        System.out.println(domain.getStores());

        System.out.println("print field data -----");
        System.out.println(domain.getSchema("company").getFieldMap());

    }

    @Test
    public void bulkInbound(){

        DcInboundDataSuo cuo = prepareData();
        Response res = apiInboundService.bulkInbound(cuo);

        if (!res.isSuccess()){
            System.out.println(res.getMessage());
        }
    }

    private DcInboundDataSuo prepareData() {
        DcInboundDataSuo inboundDataCuo = new DcInboundDataSuo();

        inboundDataCuo.setOperation(GOperation.CREATE_OR_UPDATE);
        inboundDataCuo.setSchema("fi_schema");
        inboundDataCuo.setGraph("fi_graph");
        List<Map<String, Object>> data = Lists.newArrayListWithCapacity(5);
        Map<String, Object> map1 = Maps.newHashMap();
        map1.put("object_key", "331");
        map1.put("company_name", "luban");
        map1.put("discard", "20");
//        map1.put(Keys.CTIME, "2019-01-07");

        Map<String, Object> map2 = Maps.newHashMap();
        map2.put("object_key", "321");
        map2.put("name", "houyi");
        map2.put("discard", "39");
        map2.put(Keys.CTIME, "2019-01-07");

        Map<String, Object> map3 = Maps.newHashMap();
        map3.put("object_key", "311");
        map3.put("name", "yuji");
        map3.put("discard", "38");
        map3.put(Keys.CTIME, "2019-01-07");

        Map<String, Object> map4 = Maps.newHashMap();
        map4.put("object_key", "301");
        map4.put("name", "sunshangxiang");
        map4.put("discard", "18");
        map4.put(Keys.CTIME, "2019-01-07");

        data.add(map1);
        inboundDataCuo.setRows(data);
        return inboundDataCuo;
    }

    @Test
    public void test(){
        DcInboundDataSuo cuo = new DcInboundDataSuo();
        cuo.setOperation(GOperation.CREATE_OR_UPDATE);
        cuo.setSchema("fi_schema");
        cuo.setGraph("fi_graph");
        List<Map<String, Object>> data = Lists.newArrayListWithCapacity(5);
        Map<String, Object> map1 = Maps.newHashMap();
        map1.put("object_key", "331");
        map1.put("company_name", "luban");
        map1.put("count", "3000000000000.55");
        map1.put("test2", "3000000000000");
        data.add(map1);
        cuo.setRows(data);
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
        String message = JSON.toJSONString(cuo, serializeConfig);
        System.out.println(message);
    }

}