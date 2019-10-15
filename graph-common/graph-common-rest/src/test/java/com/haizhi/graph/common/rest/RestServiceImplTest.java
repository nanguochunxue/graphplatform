package com.haizhi.graph.common.rest;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.model.v1.Response;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengangxiong on 2018/12/18
 */
public class RestServiceImplTest {

    private RestService restService;

    @Before
    public void before(){
        restService = RestFactory.getRestService();
    }

    @Test
    public void restTemplate() throws Exception {

        Map<String, Object> data = new HashMap<>();
        data.put("hello-key", "hello-value");

        DcInboundDataSuo dcInboundDataCuo = new DcInboundDataSuo();
        dcInboundDataCuo.setGraph("graph");
        dcInboundDataCuo.setOperation(GOperation.CREATE_OR_UPDATE);
        dcInboundDataCuo.setSchema("schema");

        dcInboundDataCuo.setRows(Lists.newArrayList(data));

        Response response = restService.doPost("http://localhost:10010/dc/inbound/api/bulk", dcInboundDataCuo, Response.class);

        System.out.println(response);
    }


    @Test
    public void getTest() throws Exception{
        String url = "http://192.168.1.101:9000/query/graph500/test";
        Map<String,String> parameterMap = new HashMap<>();
//        String parameter = "aaaa ffff";
        String parameter = "((t.type==\"company\" AND (t.city==\"sayan\" OR t.city==\"dongguan\")) OR (e.type==\"all_to_skill\" AND ((e.quadrant>1 AND e.quadrant<=800) OR (e.quadrant>=1000 AND e.quadrant<=3000))) OR ((t.type==\"persons\" AND (t.quadrant==4 OR t.quadrant==5)) AND (t.type==\"persons\" AND (t.quadrant==10 OR t.quadrant==20))))";
//        String paraEncode = URLEncoder.encode(parameterWrapper(parameter), "UTF-8");

        parameterMap.put("gdpQuery", parameter);
//        System.out.println("urlEncode:" + paraEncode);

//        JSONObject jsonObject = restService.doGet(url, parameterMap, JSONObject.class);
        JSONObject jsonObject = restService.doGet(url, parameterMap, JSONObject.class);
        System.out.println(JSONObject.toJSONString(jsonObject, SerializerFeature.PrettyFormat));

    }

    private static String parameterWrapper(String parameter){
        return parameter.replace(" ", "%20");
    }

}