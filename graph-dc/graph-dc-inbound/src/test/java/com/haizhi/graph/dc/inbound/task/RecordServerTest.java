package com.haizhi.graph.dc.inbound.task;

import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.common.service.TaskRedisService;
import com.haizhi.graph.dc.core.constant.Constants;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by zhengyang on 2019/5/17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class RecordServerTest {

    private static final GLog log = LogFactory.getLogger(RecordServerTest.class);

    @Autowired
    private TaskRedisService taskRedisService;

    public List<Map<String, Object>> genData(int dataSize) {
        List<Map<String, Object>> rows = new LinkedList<>();

        for (int i = 0; i < dataSize; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("object_key", String.valueOf(RandomStringUtils.randomAlphabetic(8)));
            map.put("from_key", RandomStringUtils.randomAlphabetic(15));
            map.put("to_key", RandomUtils.nextLong(1000L, 10000000L));
            map.put("double_field", RandomUtils.nextDouble(10d, 2000d));
            map.put("date_field", "2019-01-10");
            map.put("ctime", "2019-01-12");
            rows.add(map);
        }
        return rows;
    }

    @Test
    public void testSendRecords() throws IOException {
        int dataSize = 200;
        List<Map<String, Object>> totalRows = genData(dataSize);
        RecordServer recordServer = new RecordServer("http://192.168.1.57:10010/dc/inbound/api/bulk", taskRedisService, 20);
        List<List<Map<String, Object>>> recordList = recordServer.splitRows(totalRows);
        Assert.assertEquals(recordList.size(), (int) (Math.ceil((double) dataSize / Constants.BATCH_SIZE)));
        log.info("recordSize:{0}", recordList.size());
        List<DcInboundDataSuo> suoList = recordList.stream().map(rows -> {
            DcInboundDataSuo suo = new DcInboundDataSuo();
            suo.setGraph("graph_test");
            suo.setSchema("sue");
            suo.setOperation(GOperation.CREATE_OR_UPDATE);
            suo.setRows(rows);
            return suo;
        }).collect(Collectors.toList());
        recordServer.sendRecords(suoList).forEach(resp -> {
            log.info("resp:{0}", resp.isSuccess());
//            log.info("size:{}, isSuccess:{}", (int)resp.getPayload(), resp.isSuccess());
        });

    }
}
