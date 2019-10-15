package com.haizhi.graph.dc.es.consumer;

import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.dc.core.constant.DcConstants;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengangxiong on 2019/04/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class EsPersistConsumerTest {

    @Autowired
    private EsPersistConsumer consumer;

    @Test
    public void testConsumer() {
        DcInboundDataSuo suo = new DcInboundDataSuo();
        suo.setGraph("graph_chengangxiong");
        suo.setSchema("schema_my_edge");
        suo.setOperation(GOperation.CREATE_OR_UPDATE);
        suo.getHeader().getOptions().put(DcConstants.KEY_TASK_ID, 14);
        suo.getHeader().getOptions().put(DcConstants.KEY_TASK_INSTANCE_ID, 61);
        List<Map<String, Object>> rows = createData();
        suo.setRows(rows);
        consumer.doProcessMessages(suo);
    }

    private List<Map<String, Object>> createData() {
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> m1 = new HashMap<>();
        m1.put(Keys.FROM_KEY, "a1");
        m1.put(Keys.TO_KEY, "a1");
        m1.put(Keys.OBJECT_KEY, "aa");
        rows.add(m1);

        Map<String, Object> m2 = new HashMap<>();
        m2.put(Keys.FROM_KEY, "b1");
        m2.put(Keys.TO_KEY, "b1");
        m2.put(Keys.OBJECT_KEY, "bb");
        rows.add(m2);
        return rows;
    }

    @Test
    public void testConsumer1() {
        DcInboundDataSuo suo = new DcInboundDataSuo();
        suo.setGraph("crm_dev2");
        suo.setSchema("test");
        suo.setOperation(GOperation.CREATE_OR_UPDATE);
        suo.getHeader().getOptions().put(DcConstants.KEY_TASK_ID, 22);
        suo.getHeader().getOptions().put(DcConstants.KEY_TASK_INSTANCE_ID, 222);
        suo.getHeader().getOptions().put(DcConstants.ERROR_MODE, 2);
        suo.setRows(createData());
//        consumer.processMessages(StoreType.ES, suo);
    }
}