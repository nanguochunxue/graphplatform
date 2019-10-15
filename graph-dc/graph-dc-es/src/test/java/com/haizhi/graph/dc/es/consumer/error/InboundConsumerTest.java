package com.haizhi.graph.dc.es.consumer.error;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.dc.common.model.DcInboundErrorInfo;
import com.haizhi.graph.dc.common.monitor.MonitorService;
import com.haizhi.graph.dc.es.Application;
import com.haizhi.graph.dc.es.service.ErrorInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * Create by zhoumingbing on 2019-05-17
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class InboundConsumerTest {

    @Autowired
    private ErrorInfoService errorInfoService;

    @Autowired
    private MonitorService monitorService;

    @Test
    public void sendErrorInfo() {
        for (int i = 0; i < 20; i++) {
            String index = "crm_dev2";
            String type = "test_type1";
            DcInboundErrorInfo errorInfo = new DcInboundErrorInfo();
            errorInfo.setGraph(index);
            errorInfo.setSchema(type);
            errorInfo.setTaskId(1000000L);
            errorInfo.setTaskInstanceId(2000000L);
            errorInfo.setStoreType(StoreType.ES);
            errorInfo.setErrorType(DcInboundErrorInfo.ErrorType.RUNTIME_ERROR);
            errorInfo.setMsg("this is error msg");
            errorInfo.setRows(getRows());
            monitorService.errorRecord(errorInfo);
        }
    }

    @Test
    public void doRecordTest() {

        String graph = "crm_dev2";
        String schema = "sdsdf";
        Long taskId = 9999999L;
        Long taskInstanceId = 8888888L;
        DcInboundErrorInfo errorInfo = new DcInboundErrorInfo();
        errorInfo.setGraph(graph);
        errorInfo.setSchema(schema);
        errorInfo.setTaskId(taskId);
        errorInfo.setTaskInstanceId(taskInstanceId);
        errorInfo.setMsg("this is error msg! show in error detail ");
        errorInfo.setErrorType(DcInboundErrorInfo.ErrorType.CHECK_ERROR);
        errorInfo.setStoreType(StoreType.ES);
        errorInfo.setRows(getRows());
        errorInfoService.doRecord(errorInfo);
    }

    private List<Map<String, Object>> getRows() {
        List<Map<String, Object>> lists = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("object_key", 100000 + new Random().nextInt(1000));
            map.put("key1_" + i, "value1_" + i);
            map.put("key2_" + i, "value2_" + i);
            map.put("key3_" + i, "value3_" + i);
            lists.add(map);
        }
        return lists;
    }


}
