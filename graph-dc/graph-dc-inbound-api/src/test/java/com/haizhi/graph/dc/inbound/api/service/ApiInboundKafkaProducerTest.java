package com.haizhi.graph.dc.inbound.api.service;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.dc.core.constant.DcConstants;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by chengangxiong on 2019/03/08
 */
public class ApiInboundKafkaProducerTest {

    @Test
    public void producerMsg() throws ExecutionException, InterruptedException {

        DcInboundDataSuo suo = new DcInboundDataSuo();
        suo.setGraph("graph_hello");
        suo.setSchema("test_test");
        suo.setOperation(GOperation.CREATE_OR_UPDATE);
        suo.getHeader().getOptions().put(DcConstants.KEY_TASK_ID, 7L);
        suo.getHeader().getOptions().put(DcConstants.KEY_TASK_INSTANCE_ID, 533L);
        Map<String, Object> row = new HashMap<>();
        row.put("string_field", "string field");
        row.put("double_field", 4.2d);
        row.put("long_field", 33L);
        row.put("date_field", "2019-01-10");
        row.put("object_key", "xxx111xx");
        row.put("ctime", "2019-01-10");

        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(row);
        suo.setRows(rows);
        System.out.println(JSON.toJSONString(suo));

    }
}
