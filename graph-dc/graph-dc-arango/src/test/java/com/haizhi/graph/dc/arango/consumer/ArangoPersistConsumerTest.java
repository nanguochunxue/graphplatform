package com.haizhi.graph.dc.arango.consumer;

import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.dc.core.constant.DcConstants;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.common.model.DcInboundResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * Created by chengmo on 2018/11/14.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ArangoPersistConsumerTest {
    private static final GLog LOG = LogFactory.getLogger(ArangoPersistConsumerTest.class);

    @Autowired
    private ArangoPersistConsumer consumer;

    @Test
    public void testConsumer(){
        DcInboundDataSuo suo = new DcInboundDataSuo();
        suo.setGraph("graph_chengangxiong");
        suo.setSchema("schema_my_edge");
        suo.setOperation(GOperation.CREATE_OR_UPDATE);
        suo.getHeader().getOptions().put(DcConstants.KEY_TASK_ID, 14);
        suo.getHeader().getOptions().put(DcConstants.KEY_TASK_INSTANCE_ID, 61);
        List<Map<String, Object>> rows = createData();
        suo.setRows(rows);
        DcInboundResult res = consumer.doProcessMessages(suo);
        LOG.info(res);
    }

    private List<Map<String, Object>> createData() {
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> m1 = new HashMap<>();
        m1.put(Keys.FROM_KEY, "a1/dxxx");
        m1.put(Keys.TO_KEY, "a1/dxxx");
        m1.put(Keys.OBJECT_KEY, "aa");
        rows.add(m1);

        Map<String, Object> m2 = new HashMap<>();
        m2.put(Keys.FROM_KEY, "b1/cxxx");
        m2.put(Keys.TO_KEY, "b1/cxxxx");
        m2.put(Keys.OBJECT_KEY, "bb");
        rows.add(m2);
        return rows;
    }
}
