package com.haizhi.graph.plugins.flume.source.file.stage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.v1.Response;
import com.haizhi.graph.common.rest.RestService;
import com.haizhi.graph.common.rest.RestFactory;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/12/13.
 */
public class DcInboundSinkTest {
    private static final GLog LOG = LogFactory.getLogger(DcInboundSinkTest.class);

    private RestService restService;

    @Before
    public void before() {
        restService = RestFactory.getRestService();
    }

    @Test
    public void restTemplate() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("hello-key", "hello-value");
        data.put("hello-key1", "hello-value1");
        DcInboundDataSuo dcInboundDataCuo = new DcInboundDataSuo();
        dcInboundDataCuo.setSchema("schema");

        List<Map<String, Object>> lines = Lists.newArrayList(data);
        lines.add(data);
        dcInboundDataCuo.setRows(lines);
        LOG.info("line: {0}", dcInboundDataCuo);

        Response response = restService.doPost("http://localhost:10010/dc/inbound/api/bulk", dcInboundDataCuo, Response.class);
        System.out.println(response);
    }

    @Test
    public void restTemplate2() throws Exception {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hello-key", "hello-value");
        jsonObject.put("hello-key2", "hello-value2");
        jsonArray.add(jsonObject);

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("test-key", "test-value");
        jsonObject1.put("test-key2", "test-value2");
        jsonArray.add(jsonObject1);

        DcInboundDataSuo dcInboundDataCuo = new DcInboundDataSuo();
        dcInboundDataCuo.setSchema("schema");

        List<Map<String, Object>> lines = JSONUtils.toListMap(jsonArray);
        dcInboundDataCuo.setRows(lines);
        LOG.info("line: {0}", dcInboundDataCuo);
        Response response = restService.doPost("http://localhost:10010/dc/inbound/api/bulk", dcInboundDataCuo, Response.class);
        System.out.println(response);
    }

}
